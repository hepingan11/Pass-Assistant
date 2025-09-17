package com.cn.bdth.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class OrderPageVo implements Serializable {

    private IPage<Orders> orders;

    private Double totalAmount;


    @Data
    public static class Orders {

        private String ordersId;

        private String productName;

        private Long frequency;

        private LocalDateTime payTime;

        private Double productPrice;

        private Integer state;

        private String reasonFailure;

    }
}
