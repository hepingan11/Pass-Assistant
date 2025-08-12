package top.hepingan.pacommon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "link")
@Accessors(chain = true)
public class Link {

    @TableId(type = IdType.AUTO)
    private Long linkId;

    private Long userId;

    private String linkName;

    private String linkUrl;

    private String linkIntro;

    private String linkSort;

    private String linkImg;

    private Long isPublic;

    private Long isHot;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
