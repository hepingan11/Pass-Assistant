package com.cn.bdth.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.bdth.dto.MessageDto;
import com.cn.bdth.entity.ConversationUser;
import com.cn.bdth.mapper.ConversationUserMapper;
import com.cn.bdth.msg.Result;
import com.cn.bdth.service.ChatService;
import com.cn.bdth.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
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
                             @RequestParam("mcpList") String  mcpList,
                             @RequestPart(value = "file", required = false) MultipartFile file){
        MessageDto messageDto = new MessageDto()
                .setMessage(message)
                .setChatId(chatId)
                .setModel(model)
                .setIsMcp(isMcp)
                .setIsRag(isRag)
                .setMcpList(parseMcpList(mcpList))
                .setFile(file);
        try {
            return chatService.aiChat(messageDto);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/chat2",produces = "text/html; charset=UTF-8")
    public Flux<String> chat2(@RequestParam("message") String message,
                             @RequestParam("chatId") String chatId,
                             @RequestParam("model") String model,
                             @RequestParam("isMcp") Boolean isMcp,
                             @RequestParam("isRag") Boolean isRag,
                             @RequestParam("mcpList") String  mcpList,
                             @RequestPart(value = "file", required = false) MultipartFile file){
        MessageDto messageDto = new MessageDto()
                .setMessage(message)
                .setChatId(chatId)
                .setModel(model)
                .setIsMcp(isMcp)
                .setIsRag(isRag)
                .setMcpList(parseMcpList(mcpList))
                .setFile(file);
        return chatService.aiChat2(messageDto);
    }

    private List<Long> parseMcpList(String mcpListStr) {
        if (mcpListStr == null || mcpListStr.isEmpty()) {
            return List.of();
        }
        // 移除方括号并按逗号分割
        String cleaned = mcpListStr.replaceAll("[\\[\\]]", "");
        if (cleaned.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());
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

    /**
     * 获取会话列表
     * @return
     */
    @GetMapping(value = "/conversation/list")
    public Result getConversationList(){
        return Result.data(chatService.conversationList());
    }

    /**
     * 添加会话
     * @param messageDto
     * @return
     */
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


    /**
     * 修改会话
     * @param messageDto
     * @return
     */
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

    /**
     * 上传 rag文件
     * @param file
     * @return
     */
    @PostMapping(value = "/rag/upload")
    public Result updateRag(@RequestParam("file") MultipartFile file){
        try {
            chatService.updateRag(file);
            return Result.ok();
        }catch (Exception e){
            return Result.error("修改失败");
        }
    }

    /**
     * rag列表
     * @return
     */
    @GetMapping(value = "/rag/list")
    public Result ragList(){
        return Result.data(chatService.ragList());
    }

    /**
     * 删除 rag
     * @param ragId
     * @return
     */
    @GetMapping(value = "/rag/delete/{ragId}")
    public Result deleteRag(@PathVariable Long ragId){
        try {
            chatService.deleteRag(ragId);
            return Result.ok();
        }catch (Exception e){
            return Result.error("删除失败");
        }
    }

    /**
     * 修改是否启用 rag
     * @param ragId
     * @return
     */
    @PostMapping(value = "/rag/toggle/{ragId}")
    public Result toggleRag(@PathVariable Long ragId){
        try {
            chatService.toggleRag(ragId);
            return Result.ok();
        }catch (Exception e){
            return Result.error("删除失败");
        }
    }

    /**
     * mcp列表
     * @return
     */
    @GetMapping(value = "/mcp/list")
    public Result mcpList(){
        return Result.data(chatService.mcpList());
    }

}
