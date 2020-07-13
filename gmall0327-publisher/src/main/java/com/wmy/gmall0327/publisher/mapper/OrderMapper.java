package com.wmy.gmall0327.publisher.mapper;

import java.util.List;
import java.util.Map;

public interface OrderMapper {
    // 求单日交易额
    public Double selectOrderAmount(String date);

    // 求单日分时交易额
    public List<Map> selectOrderAmountHour(String date);
}
