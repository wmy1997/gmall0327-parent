package com.wmy.gmall0327.logger.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoggerController {

    @PostMapping("log")

    public String log( @RequestParam("logString") String logString){

        System.out.println(logString);

        //补时间戳 为了统一时间，用户手机时间可能不准 就统一在系统收到的时候加个时间
        JSONObject jsonObject = JSON.parseObject(logString);
        jsonObject.put("ts",System.currentTimeMillis());

        String jsonString = jsonObject.toJSONString();

        // 为防止没有初始化声明 在idea需装个插件lombok
        log.info(jsonString);
        return "success";
    }
}
