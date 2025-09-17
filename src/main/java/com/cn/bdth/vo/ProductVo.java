package com.cn.bdth.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Data
@Accessors(chain = true)
public class ProductVo {

    private Long productId;

    private String productName;

    private Long frequency;

    private Double productPrice;

    private LocalDateTime createdTime;
}
