package com.cn.bdth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author hepingan
 */
@Data
@Accessors(chain = true)
public class DigitalVideoDto {

    @NotBlank(message = "数字人名称不能为空")
    private String videoName;

    @NotNull(message = "视频地址不能为空")
    private String videoUrl;

    @NotNull(message = "上传人不能为空")
    private Long userId;

    @NotNull(message = "视频封面地址不能为空")
    private String imgUrl;
}
