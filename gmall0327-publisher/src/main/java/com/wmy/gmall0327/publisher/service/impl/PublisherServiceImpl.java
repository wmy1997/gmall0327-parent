package com.wmy.gmall0327.publisher.service.impl;

import com.alibaba.fastjson.JSON;
import com.wmy.gmall0327.publisher.mapper.DauMapper;
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

    @Override
    public Long getDauTotal(String date) {
        Long dauTotal = dauMapper.getDauTotal(date);
        return dauTotal;
    }

    @Override
    public Map getDauHour(String date) {
        List<Map> dauHourMapList = dauMapper.getDauHour(date);
        HashMap dauHourMap = new HashMap();

        for (Map map : dauHourMapList) {
            dauHourMap.put(map.get("loghour"),map.get("ct"));
        }

        return dauHourMap;
    }

}
