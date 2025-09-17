package com.cn.bdth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "mcps")
public class Mcps {

    @TableId(type = IdType.AUTO)
    private Long mcpsId;

    private String methodName;

    private String mcpName;

    private String introduce;

    private String icon;

    private LocalDateTime createdTime;
}
