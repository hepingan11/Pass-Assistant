package com.cn.bdth.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.bdth.entity.SpringAiChatMemory;
import com.cn.bdth.mapper.SpringAiChatMemoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MysqlChatMemory implements ChatMemory {

    private final SpringAiChatMemoryMapper springAiChatMemoryMapper;

    @Override
    public void add(String conversationId, Message message){

    }

    @Override
    public void add(String conversationId, List<Message> messages) {

    }

    @Override
    public List<Message> get(String conversationId) {
        List<SpringAiChatMemory> springAiChatMemories = springAiChatMemoryMapper.selectList(new QueryWrapper<SpringAiChatMemory>().lambda()
                .eq(SpringAiChatMemory::getConversationId, conversationId)
                .orderByDesc(SpringAiChatMemory::getTimestamp)
                .last("LIMIT 20"));
        Collections.reverse(springAiChatMemories);
        List<Message> messages = new ArrayList<>();
        for (SpringAiChatMemory springAiChatMemory : springAiChatMemories){
            String type = springAiChatMemory.getType();
            switch (type){
                case "USER" -> messages.add(new UserMessage(springAiChatMemory.getContent()));
                case "ASSISTANT" -> messages.add(new AssistantMessage(springAiChatMemory.getContent()));
                case "SYSTEM" -> messages.add(new SystemMessage(springAiChatMemory.getContent()));
                default -> throw new IllegalArgumentException("Unknown message type:"+type);
            }
        }
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        springAiChatMemoryMapper.delete(new QueryWrapper<SpringAiChatMemory>().lambda()
                .eq(SpringAiChatMemory::getConversationId, conversationId));
    }

}
