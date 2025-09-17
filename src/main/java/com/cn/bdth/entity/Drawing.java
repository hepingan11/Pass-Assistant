package com.cn.bdth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 绘图实体类
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Data
@TableName(value = "drawing")
@Accessors(chain = true)
public class Drawing {

    @TableId(type = IdType.AUTO)
    private Long drawingId;

    private Long userId;

    private String prompt;

    private String generateUrl;

    private Boolean isPublic;

    private String model;

    private String size;

    private String image;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;



}
