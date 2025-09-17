package com.cn.bdth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Accessors(chain = true)
public class CodeDto {

    private Long codeId;

    @NotNull(message = "项目名不能为空")
    private String codeName;

    private String introduce;

    private String language;

    @NotNull(message = "价格不能为空")
    private Double price;

    private List<MultipartFile> imageList;

    private List<String> imageUrlList;

    @NotNull(message = "下载地址不能为空")
    private String downloadUrl;

    private String extractCode;
}
