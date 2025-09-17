package com.cn.bdth.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Data
@TableName(value = "orders")
/**
 * 使用 Lombok 的 @Accessors(chain = true) 注解可以实现链式调用（方法返回当前对象），
 * 例如：orders.setOrdersId("123").setUserId(1L);
 */
@Accessors(chain = true)
public class Orders {


    @TableId(type = IdType.INPUT)
    private String ordersId;


    private Long userId;


    private Long productId;


    private String productName;


    private Double productPrice;


    private Long frequency;

    private Integer state;

    private String reasonFailure;

    private LocalDateTime payTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
