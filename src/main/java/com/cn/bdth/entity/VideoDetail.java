package com.cn.bdth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cn.bdth.dto.DoubaoVideoCallback;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName(value = "video_detail")
public class VideoDetail {

    @TableId(type = IdType.AUTO)
    private Long videoDetailId;

    private Long videoId;

    //任务使用的模型名称
    private String model;

    //本次请求使用的种子整数值。
    private Integer seed;

    //生成视频的分辨率。
    private String resolution;

    //生成视频的宽高比。
    private Integer duration;

    //生成视频的时长，单位：秒。
    private String ratio;

    //生成视频的帧率。
    private Integer framespersecond;

    //模型生成的 token 数量。
    private Integer completion_tokens;
}
