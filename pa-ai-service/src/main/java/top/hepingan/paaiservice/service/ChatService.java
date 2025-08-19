package top.hepingan.paaiservice.service;


import reactor.core.publisher.Flux;
import top.hepingan.pacommon.dto.MessageDto;
import top.hepingan.pacommon.entity.ConversationUser;
import top.hepingan.pacommon.entity.SpringAiChatMemory;

import java.util.List;

/**
 * 雨纷纷旧故里草木深
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
public interface ChatService {


    List<SpringAiChatMemory> getHistory(String chatId);

    void deleteHistory(String chatId);

    List<ConversationUser> conversationList();

    Flux<String> aiChat(MessageDto messageDto);
}
