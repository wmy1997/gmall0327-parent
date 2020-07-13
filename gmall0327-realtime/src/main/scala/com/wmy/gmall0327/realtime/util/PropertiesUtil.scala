package com.wmy.gmall0327.realtime.util

import java.io.InputStreamReader
import java.util.Properties

/**
 * @Author Wangmy
 * @Description 自定义一个获取配置文件的类
 * @Date 2020/6/11 22:15
 * @Param 读取的配置文件
 * @return 返回配置文件对象
 **/
object PropertiesUtil {

  def main(args: Array[String]): Unit = {
    val properties: Properties = PropertiesUtil.load("config.properties")

    println(properties.getProperty("kafka.broker.list"))
  }

  def load(propertieName:String): Properties = {
    val prop = new Properties();
    prop.load(new InputStreamReader(Thread.currentThread().getContextClassLoader.getResourceAsStream(propertieName) , "UTF-8"))
    prop
  }

}
