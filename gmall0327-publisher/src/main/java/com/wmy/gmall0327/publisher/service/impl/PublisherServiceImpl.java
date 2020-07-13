package com.wmy.gmall0327.publisher.service.impl;

import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.MAC;
import com.wmy.gmall0327.publisher.mapper.DauMapper;
import com.wmy.gmall0327.publisher.mapper.OrderMapper;
import com.wmy.gmall0327.publisher.service.PublisherService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.phoenix.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    DauMapper dauMapper;
    @Autowired
    OrderMapper orderMapper;

    @Override
    public Long getDauTotal(String date) {
        Long dauTotal = dauMapper.getDauTotal(date);
        return dauTotal;
    }

    @Override
    public Map getDauHour(String date) {
        List<Map> dauHourMapList = dauMapper.getDauHour(date); // [{"loghour":11,"ct":123},{"loghour":12,"ct":3434}...]
        HashMap dauHourMap = new HashMap(); //{"11":123,"12",3434}

        for (Map map : dauHourMapList) {
            dauHourMap.put(map.get("loghour"),map.get("ct"));
        }

        return dauHourMap;
    }

    @Override
    public Double getOrderAmount(String date) {
        Double orderAmount = orderMapper.selectOrderAmount(date);
        return orderAmount;
    }

    @Override
    public Map getOrderAmountHour(String date) {
        List<Map> orderAmountHour = orderMapper.selectOrderAmountHour(date);
        HashMap hourAmountMap = new HashMap();
        for (Map map : orderAmountHour) {
            hourAmountMap.put(map.get("CREATE_HOUR"), map.get("ORDER_AMOUNT"));
        }

        return hourAmountMap;
    }

}
