package top.hepingan.paaiservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import top.hepingan.paaiservice.service.ChatService;
import top.hepingan.pacommon.entity.SpringAiChatMemory;
import top.hepingan.pacommon.mapper.SpringAiChatMemoryMapper;
import top.hepingan.pacommon.msg.Result;
import top.hepingan.pacommon.utils.UserUtils;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@Slf4j
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {


    private final ChatClient chatClient;

    private final VectorStore vectorStore;

    private final ChatService chatService;

    /**
     * ai聊天
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/chat",produces = "text/html; charset=UTF-8")
    public Flux<String> chat(@RequestParam("message") String message, @RequestParam("chatId") String chatId){
        return chatClient.prompt()
                .user(message)
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .topK(5)//最多 5 个检索结果
                                .similarityThreshold(0.6)//最小相似度匹配百分比
                                .build()).build())
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    /**
     * 获取历史记录
     * @param chatId
     * @param pageNum
     * @return
     */
    @GetMapping("/history")
    public Result getHistory(String chatId,Integer pageNum){
        return Result.data(chatService.getHistory(chatId,pageNum));
    }
}
