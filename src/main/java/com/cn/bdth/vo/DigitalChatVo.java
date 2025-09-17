package com.cn.bdth.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DigitalChatVo {

    private String msg;

    private String videoUrl;

}
