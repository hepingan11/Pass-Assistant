package top.hepingan.pacommon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 何平安
 */
@Data
@TableName(value = "digital")
@Accessors(chain = true)
public class Digital {

    @TableId(type = IdType.AUTO)
    private Long videoId;

    private String videoName;

    private String videoUrl;

    private String imgUrl;

    private Long userID;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
