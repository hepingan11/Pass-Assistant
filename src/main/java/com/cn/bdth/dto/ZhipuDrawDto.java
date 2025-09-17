package com.cn.bdth.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

@Data
@Accessors(chain = true)
public class ZhipuDrawDto {
    private String model;

    private String prompt;

    private String quality;

    private String size;

    private String image;

}
