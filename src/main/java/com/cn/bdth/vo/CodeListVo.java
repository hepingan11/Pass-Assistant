package com.cn.bdth.vo;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CodeListVo {
    private Long codeId;

    private String codeName;

    private Long userId;

    private Integer look;

    private String introduce;

    private Integer isPublic;

    private Double price;

    private String downloadUrl;

    private String language;

    private List<String> imageList;

    private LocalDateTime createdTime;

    private LocalDateTime updateTime;

}
