package top.hepingan.pacommon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("spring_ai_chat_memory")
public class SpringAiChatMemory {

    private String conversationId;

    private String content;

    private String type;

    private LocalDateTime timestamp;
}
