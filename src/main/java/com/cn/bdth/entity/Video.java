package com.cn.bdth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "video")
public class Video {

    @TableId(type = IdType.AUTO)
    private Long videoId;

    private String prompt;

    private String status;

    private String videoUrl;

    private String imgUrl;

    private Long userId;

    private String taskId;

    private String coverUrl;

    private Boolean isPublic;

    private LocalDateTime createdTime;
}
