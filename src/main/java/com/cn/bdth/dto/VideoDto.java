package com.cn.bdth.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

@Data
@Accessors(chain = true)
public class VideoDto {

    private String prompt;

    private MultipartFile file;
}
