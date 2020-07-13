package com.wmy.gmall0327.realtime.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.wmy.gmall0327.common.constant.GmallConstant
import com.wmy.gmall0327.realtime.bean.StartUpLog
import com.wmy.gmall0327.realtime.util.MyKafkaUtil
import org.apache.hadoop.conf.Configuration
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis
// 引入此包才能使用phoenix
import org.apache.phoenix.spark._

object DauApp {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("DauApp")
    val ssc: StreamingContext = new StreamingContext(sparkConf, Seconds(5))

    val inputDstream: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(GmallConstant.KAFKA_TOPIC_STARTUP, ssc)

    /*    inputDstream.foreachRDD(rdd=>{
          println(rdd.map(_.value()).collect().mkString("\n")) 打印测试先看一下
        })*/

    // 去重
    // 1 当日用户访问的清单保存到redis中
    /*
    这边redis有5大数据结构 选择哪种结构进行存储呢？
    string 一对一，本次需求是一个时间对应多个用户，排除
    list   有重复
    set    没有重复
    zset   不需要排序，排除
    hash    假如需要在对key进一步说明，则使用它 比如（k1,(k2,v)) k1作为日期，k2是name v是具体名字
    最终： type:set    key: day2020-06-11   value:用户id(mid设备id)
     */
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH")

    val startUplogDstream: DStream[StartUpLog] = inputDstream.map(recored => {
      val jsonString: String = recored.value()
      val startUplog: StartUpLog = JSON.parseObject(jsonString, classOf[StartUpLog])
      val date: Date = new Date(startUplog.ts)
      val dateHour: String = sdf.format(date)
      val dateArr: Array[String] = dateHour.split(" ")
      startUplog.logDate = dateArr(0)
      startUplog.logHour = dateArr(1)
      startUplog
    })

    /*
    transform算子在每一批次都会执行一次，也就是说每批次driver获取到redis的key，然后广播到executor，
    减少了executor每次读取redis链接的消耗。
     */
    val filter1DStream: DStream[StartUpLog] = startUplogDstream.transform { rdd =>
      //--- 周期性的查询redis的清单 通过广播变量(量不能太大) 发送到executor
      println("过滤前"+rdd.count())
      val jedis: Jedis = new Jedis("linux01", 6379)
      // 以当前时间作为key
      val dateStr: String = new SimpleDateFormat("yyyy-MM-dd").format(new Date)
      // ***** 这边dau应该是dau: *****
      val dateKey: String = "dau" + dateStr
      val dauMidSet: util.Set[String] = jedis.smembers(dateKey)
      jedis.close()
      val dauMidBC: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(dauMidSet)

      //--- 以上代码在driver端执行
      val filterRDD: RDD[StartUpLog] = rdd.filter { startUpLog =>
        val midSet: util.Set[String] = dauMidBC.value
        !midSet.contains(startUpLog.mid)
      }
      println("过滤后"+filterRDD.count())
      filterRDD
    }

    /*
    虽然redis的set集合可以跨批次的去重，但同批次的去重redis则做不到 需要手动去重
     */
    val startupGroupbyMidDstream: DStream[(String, Iterable[StartUpLog])] = filter1DStream.map{startUpLog=>(startUpLog.mid,startUpLog)}.groupByKey()

    // 只保留时间最小的那一条
    val filter2DStream: DStream[StartUpLog] = startupGroupbyMidDstream.flatMap { case (mid, startuplogIter) =>
      val sortList: List[StartUpLog] = startuplogIter.toList.sortWith { (s1, s2) =>
        s1.ts < s2.ts
      }
      sortList.take(1)
    }

    filter2DStream.cache()

    // 保存清单
    // redis type=set key=dau:2020.329 value=mid
    filter2DStream.foreachRDD(rdd => {
      //这里要是有代码，在driver执行

      rdd.foreachPartition(startUplogIter => {
        // startUplogIter是一个Iterator 表示每一个分区的数据
        //这里面的代码在executor执行 链接次数大大减小，一个分区才创建一次链接
        val jedis: Jedis = new Jedis("linux01", 6379)

        for (startUplog <- startUplogIter) {
          println(startUplog)

          // ***** 这边dau应该是dau: *****
          val dateKey: String = "dau" + startUplog.logDate
          // set的写入
          jedis.sadd(dateKey, startUplog.mid)
        }
        jedis.close()

      })
    })

    // 将每批次的数据通过phoenix写入Hbase
    filter2DStream.foreachRDD{rdd=>
      rdd.saveToPhoenix("GMALL0327_DAU",Seq("MID", "UID", "APPID", "AREA", "OS", "CH", "TYPE", "VS", "LOGDATE", "LOGHOUR", "TS"),new Configuration,Some("linux01,linux02,linux03:2181"))
    }

    ssc.start()
    ssc.awaitTermination()
  }
}
