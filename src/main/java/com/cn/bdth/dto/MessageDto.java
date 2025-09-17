package com.cn.bdth.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Accessors(chain = true)
public class MessageDto {

    private String message;

    private String chatId;

    private String title;

    private String model;

    private Boolean isRag;

    private Boolean isMcp;

    private List<Long> mcpList;

    private String role;

    private MultipartFile file;
}
