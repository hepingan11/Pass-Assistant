package com.cn.bdth.dto;

import com.cn.bdth.model.ZhipuModel;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ZhipuWebDto {

    @NotEmpty(message = "消息数据不能为空")
    private List<ZhipuModel> messages;
}
