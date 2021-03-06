package com.wmy.gmall0327.publisher.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface PublisherService {

    public Long getDauTotal(String date);
    public Map getDauHour(String date);

    // 求单日交易额
    public Double getOrderAmount(String date);

    // 求单日分时交易额
    public Map getOrderAmountHour(String date);
}
