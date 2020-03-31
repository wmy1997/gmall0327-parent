package com.wmy.gmall0327.publisher.controller;

import com.alibaba.fastjson.JSON;
import com.wmy.gmall0327.publisher.service.PublisherService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class PublisherController {

    @Autowired
    PublisherService publisherService;

    @GetMapping("realtime-total")
    public String getRealtimeTotal(@RequestParam("date") String date){

        Long dauTotal = publisherService.getDauTotal(date);
        List<Map> totalList = new ArrayList();
        HashMap dauMap = new HashMap();
        dauMap.put("id","dau");
        dauMap.put("name","新增日活");
        dauMap.put("value",dauTotal);
        totalList.add(dauMap);

        HashMap newMidMap = new HashMap();
        newMidMap.put("id","new_mid");
        newMidMap.put("name","新增设备");
        newMidMap.put("value",233);
        totalList.add(newMidMap);

        return JSON.toJSONString(totalList);
    }

    @GetMapping("realtime-hour")
    public String getRealtimeHour(@RequestParam("id") String id ,@RequestParam("date") String date){
        if("dau".equals(id)){

            Map dauHourMapTD = publisherService.getDauHour(date);
            String yd = getYd(date);
            Map dauHourMapYD = publisherService.getDauHour(yd);

            HashMap map= new HashMap();
            map.put("yesterday",dauHourMapYD);
            map.put("today",dauHourMapTD);
            return JSON.toJSONString(map);
        }else {
            return null;
        }


    }

    private String getYd(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date td = sdf.parse(date);
            Date yd = DateUtils.addDays(td, -1);
            return sdf.format(yd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
