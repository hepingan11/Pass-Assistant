package com.cn.bdth.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class DigitalChatDto {

    @NotNull(message = "数字人ID不能为空")
    private Long videoId;

    @NotNull(message = "发送信息不能为空")
    private String msg;

    @NotNull(message = "发送用户ID不能为空")
    private Long userId;
}

