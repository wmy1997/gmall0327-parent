package com.wmy.gmall0327.publisher.mapper;

import java.util.List;
import java.util.Map;

public interface DauMapper {

    // 求日活总数
    public Long getDauTotal(String date);

    // 求日活的分时总数
    public List<Map> getDauHour(String date);
}
