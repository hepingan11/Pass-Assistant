package top.hepingan.pacommon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EmailContentDto {
    @NotNull(message = "称呼不能为空")
    private String name;

    @NotNull(message = "联系方式不能为空")
    private String email;

    @NotNull(message = "内容不能为空")
    private String content;
}
