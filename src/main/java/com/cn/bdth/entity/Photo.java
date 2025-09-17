package com.cn.bdth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "photo")
@Accessors(chain = true)
public class Photo {

    @TableId(type = IdType.AUTO)
    private Long photoId;

    private String url;

    private String name;

    private Long userId;

    private Integer isPublic;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
