package com.cn.bdth.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Data
public class UserPutDto {

    @NotNull(message = "用户次数不能为空")
    private Long frequency;

    @NotNull(message = "用户标识不能为空")
    private Long userId;
}
