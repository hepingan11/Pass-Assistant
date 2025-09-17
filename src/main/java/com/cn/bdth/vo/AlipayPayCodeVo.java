package com.cn.bdth.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Accessors(chain = true)
public class AlipayPayCodeVo implements Serializable {

    private String ordersId;

    private String productName;

    private LocalDateTime createdTime;

    private Double productPrice;

    private String qrCode;

    private String payUrl;

}
