package com.cn.bdth.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NewsVo {
    private String title;

    private String time;

    private String time2;

    private String img;

    private String text;
}
