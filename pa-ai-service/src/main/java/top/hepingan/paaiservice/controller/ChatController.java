package top.hepingan.paaiservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import top.hepingan.paaiservice.service.ChatService;
import top.hepingan.pacommon.dto.MessageDto;
import top.hepingan.pacommon.entity.ConversationUser;
import top.hepingan.pacommon.mapper.ConversationUserMapper;
import top.hepingan.pacommon.msg.Result;
import top.hepingan.pacommon.utils.UserUtils;

import java.time.LocalDateTime;


@RestController
@Slf4j
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {


    private final ChatService chatService;

    private final ConversationUserMapper conversationUserMapper;

    /**
     * AI聊天
     * @return
     */
    @PostMapping(value = "/chat",produces = "text/html; charset=UTF-8")
    public Flux<String> chat(@RequestParam("message") String message,
                             @RequestParam("chatId") String chatId,
                             @RequestParam("model") String model,
                             @RequestParam("isMcp") Boolean isMcp,
                             @RequestParam("isRag") Boolean isRag,
                             @RequestPart(value = "file", required = false) MultipartFile file){
        MessageDto messageDto = new MessageDto()
                .setMessage(message)
                .setChatId(chatId)
                .setModel(model)
                .setIsMcp(isMcp)
                .setIsRag(isRag)
                .setFile(file);
        return chatService.aiChat(messageDto);
    }


    /**
     * 获取聊天记录
     * @param chatId
     * @return
     */
    @GetMapping(value = "/history/{chatId}")
    public Result getHistory(@PathVariable String chatId){
        return Result.data(chatService.getHistory(chatId));
    }

    /**
     删除聊天记录
     */
    @PostMapping(value = "/delete")
    public Result delete(@RequestBody String chatId){
        try {
            chatService.deleteHistory(chatId);
            return Result.ok();
        }catch (Exception e){
            return Result.error("删除失败");
        }
    }

    @GetMapping(value = "/conversation/list")
    public Result getConversationList(){
        return Result.data(chatService.conversationList());
    }

    @PostMapping(value = "/conversation/add")
    public Result addConversation(@RequestBody MessageDto messageDto){
        try {
            conversationUserMapper.insert(new ConversationUser()
                    .setConversationId(messageDto.getChatId())
                    .setCreatedTime(LocalDateTime.now())
                    .setTitle(messageDto.getTitle())
                    .setUserId(UserUtils.getCurrentLoginId()));
            return Result.ok();
        }catch (Exception e){
            return Result.error("添加失败");
        }

    }


    @PostMapping(value = "/conversation/update")
    public Result updateConversation(@RequestBody MessageDto messageDto){
        try {
            conversationUserMapper.update(new ConversationUser()
                    .setRole(messageDto.getRole())
                    .setTitle(messageDto.getTitle()),new QueryWrapper<ConversationUser>().lambda()
                    .eq(ConversationUser::getConversationId, messageDto.getChatId())
            );
            return Result.ok();
        }catch (Exception e){
            return Result.error("修改失败");
        }
    }
}
