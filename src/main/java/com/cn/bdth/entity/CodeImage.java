package com.cn.bdth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "code_image")
public class CodeImage {

    @TableId(type = IdType.AUTO)
    private Long codeImageId;

    private Long codeId;

    private String imageUrl;
}
