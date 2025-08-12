package top.hepingan.pacommon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("code")
public class Code {

    @TableId(type = IdType.AUTO)
    private Long codeId;

    private String codeName;

    private Long userId;

    private Integer look;

    private String introduce;

    private Integer isPublic;

    private Double price;

    private String downloadUrl;

    private String language;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
