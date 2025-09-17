package com.cn.bdth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "rag")
public class Rag {

    @TableId(type = IdType.AUTO)
    private Long ragId;

    private String ragUrl;

    private Long userId;

    private String ragName;

    private Boolean isEnable;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
