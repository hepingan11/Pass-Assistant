package top.hepingan.paaiservice.service;


import top.hepingan.pacommon.entity.SpringAiChatMemory;

import java.util.List;

/**
 * 雨纷纷旧故里草木深
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
public interface ChatService {


    List<SpringAiChatMemory> getHistory(String chatId,Integer pageNum);
}
