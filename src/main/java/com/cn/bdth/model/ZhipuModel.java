package com.cn.bdth.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ZhipuModel {
    private String model;

    private int top_p = 1;

    private List<Map<String, Object>> messages;

    private boolean stream = true;

    private int max_tokens = 4096;

    private int temperature = 1;


}


