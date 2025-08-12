package top.hepingan.pacommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(value = "dialogue")
@Accessors(chain = true)
public class Dialogue {
    @TableId(type = IdType.AUTO)
    private Long dialogueId;

    private Long userId;

    private String content;

    private String message;

    private LocalDateTime createdTime;
}
