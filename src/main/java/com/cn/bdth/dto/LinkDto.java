package com.cn.bdth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LinkDto {

    @NotNull(message = "链接名称不能为空")
    private String linkName;

    @NotNull(message = "链接地址不能为空")
    private String linkUrl;

    @NotNull(message = "链接简介不能为空")
    private String linkIntro;

    @NotNull(message = "链接图片不能为空")
    private MultipartFile images;

    @NotNull(message = "链接分类不能为空")
    private String linkSort;
}
