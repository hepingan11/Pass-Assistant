package top.hepingan.pacommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "spring_ai_chat_memory")
public class SpringAiChatMemory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String conversationId;

    private String content;

    private String media;

    private String type;

    private LocalDateTime timestamp;

    private String model;

    private Boolean isMcp;

    private Boolean isRag;
}
