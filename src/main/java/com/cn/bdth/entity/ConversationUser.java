package com.cn.bdth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "conversation_user")
public class ConversationUser {

    private String conversationId;

    private Long userId;

    //对话标题
    private String title;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String role;

}
