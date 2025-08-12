package top.hepingan.paaiservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.hepingan.paaiservice.service.ChatService;
import top.hepingan.pacommon.entity.SpringAiChatMemory;
import top.hepingan.pacommon.mapper.SpringAiChatMemoryMapper;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final SpringAiChatMemoryMapper springAiChatMemoryMapper;

    /**
     * 获取历史记录
     * @param chatId
     * @param pageNum
     * @return
     */
    @Override
    public List<SpringAiChatMemory> getHistory(String chatId,Integer pageNum) {
        Page<SpringAiChatMemory> page = new Page<>(pageNum, 20);
        return springAiChatMemoryMapper.selectPage(page,new QueryWrapper<SpringAiChatMemory>().lambda()
                .eq(SpringAiChatMemory::getConversationId, chatId)
                .orderByDesc(SpringAiChatMemory::getTimestamp)).getRecords();
    }
}
