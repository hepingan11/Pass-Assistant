package top.hepingan.pacommon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "work")
@Accessors(chain = true)
public class Work {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long workId;

    private String name;

    private String url;

    private String sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
