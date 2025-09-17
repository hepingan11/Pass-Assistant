package com.cn.bdth.vo.admin;

import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class DrawDetailVo {

    private Long drawingId;

    private Long userId;//用户id

    private String userName;//用户姓名

    private String avatar;//用户头像

    private String prompt;//使用的提示词

    private String generateUrl;//图片url

    private String model;

    private String size;

    private String image;

    private Boolean isPublic;

    private LocalDateTime createdTime;
}
