package top.hepingan.paaiservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import top.hepingan.paaiservice.common.MysqlChatMemory;
import top.hepingan.paaiservice.service.ChatService;
import top.hepingan.pacommon.constants.AiModelConstant;
import top.hepingan.pacommon.dto.MessageDto;
import top.hepingan.pacommon.entity.ConversationUser;
import top.hepingan.pacommon.entity.SpringAiChatMemory;
import top.hepingan.pacommon.mapper.ConversationUserMapper;
import top.hepingan.pacommon.mapper.SpringAiChatMemoryMapper;
import top.hepingan.pacommon.utils.AliUploadUtils;
import top.hepingan.pacommon.utils.UserUtils;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final SpringAiChatMemoryMapper springAiChatMemoryMapper;

    private final ConversationUserMapper conversationUserMapper;

    private final VectorStore vectorStore;

    private final ZhiPuAiChatModel zhiPuAiChatModel;

    private final OpenAiChatModel deepseekModel;

    private final OpenAiChatModel qwenModel;

    private final OpenAiChatModel gorkModel;

    private final OpenAiChatModel geminiModel;

    private final AliUploadUtils aliUploadUtils;

    private final OpenAiChatModel openaiModel;

    private final OpenAiChatModel claudeModel;

    private final ToolCallbackProvider toolCallbackProvider;

    private final MysqlChatMemory chatMemory;

    private final OpenAiChatModel doubaoModel;


    @Value("${ali-oss.domain}")
    private String domin;

    @Override
    public List<SpringAiChatMemory> getHistory(String chatId) {
        return springAiChatMemoryMapper.selectList(new QueryWrapper<SpringAiChatMemory>().lambda()
                .eq(SpringAiChatMemory::getConversationId, chatId));
    }

    @Override
    public void deleteHistory(String chatId) {
        String modifiedChatId = chatId.substring(0, chatId.length() - 1);
        springAiChatMemoryMapper.delete(new QueryWrapper<SpringAiChatMemory>().lambda()
                .eq(SpringAiChatMemory::getConversationId, modifiedChatId));
        conversationUserMapper.delete(new QueryWrapper<ConversationUser>().lambda()
                .eq(ConversationUser::getConversationId, modifiedChatId));

    }

    @Override
    public List<ConversationUser> conversationList() {
        return conversationUserMapper.selectList(new QueryWrapper<ConversationUser>().lambda()
                .eq(ConversationUser::getUserId, UserUtils.getCurrentLoginId())
                .orderByDesc(ConversationUser::getCreatedTime));
    }


    @Override
    public Flux<String> aiChat(MessageDto messageDto) {
        ConversationUser conversationUser = conversationUserMapper.selectOne(new QueryWrapper<ConversationUser>().lambda()
                .eq(ConversationUser::getConversationId, messageDto.getChatId()));
        Long currentLoginId = UserUtils.getCurrentLoginId();

        if (conversationUser == null){
            conversationUserMapper.insert(new ConversationUser()
                    .setConversationId(messageDto.getChatId())
                    .setCreatedTime(LocalDateTime.now())
                    .setTitle(LocalDateTime.now().toString().substring(5,10)+"新对话")
                    .setUserId(currentLoginId));
        }

        // 如果messageDto中的role字段为null，则设置默认角色描述；否则使用传入的role值
        String role="你名字叫何平安，是一个高冷酷酷的IT高手";
        if (conversationUser != null) {
            role = conversationUser.getRole() == null ? "你名字叫何平安，是一个高冷酷酷的IT高手" : conversationUser.getRole();
        }

        if (messageDto.getModel() ==null || messageDto.getModel().isEmpty()){
            messageDto.setModel("DEEPSEEK");
        }

        switch (messageDto.getModel()) {
            case "GPT"->{
                if (messageDto.getFile() == null){
                    return ChatWithoutFile(messageDto, openaiModel, AiModelConstant.GPT,role);
                }else {
                    return ChatWithFile(messageDto, openaiModel, AiModelConstant.GPT);
                }
            }
            case "CLAUDE" ->{
                if (messageDto.getFile() == null){
                    return ChatWithoutFile(messageDto, claudeModel, AiModelConstant.CLAUDE,role);
                }else {
                    return ChatWithFile(messageDto, claudeModel, AiModelConstant.CLAUDE);
                }

            }
            case "DEEPSEEK" -> {
                return ChatWithoutFile(messageDto, deepseekModel,AiModelConstant.DEEPSEEK,role);
            }
            case "QWEN" ->{
                return ChatWithoutFile(messageDto,qwenModel,AiModelConstant.QWEN,role);
            }
            case "GEMINI" -> {
                return ChatWithoutFile(messageDto,geminiModel,AiModelConstant.GEMINI,role);
            }
            case "GROK" ->{
                return ChatWithoutFile(messageDto,gorkModel,AiModelConstant.GROK,role);
            }
            case "DEEPSEEK_R"->{
                return ChatWithoutFile(messageDto,deepseekModel,AiModelConstant.DEEPSEEK_R,role);
            }
            case "DOUBAO" ->{
                if (messageDto.getFile() == null){
                    return ChatWithoutFile(messageDto, doubaoModel, AiModelConstant.DOUBAO,role);
                }else {
                    return ChatWithFile(messageDto, doubaoModel, AiModelConstant.DOUBAO);
                }
            }
            case "GLM" ->{
                if (messageDto.getFile() == null){
                    ChatClient chatClient;
                    if (messageDto.getIsMcp()){
                        //是否开启了RAG
                        if (messageDto.getIsRag()){
                            chatClient = ChatClient.builder(zhiPuAiChatModel)
                                    .defaultSystem(role)
                                    .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                                            .searchRequest(SearchRequest.builder()
                                                    .topK(5)//最多 5 个检索结果
                                                    .similarityThreshold(0.6)//最小相似度匹配百分比
                                                    .build()).build())
                                    .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                    .defaultToolCallbacks(toolCallbackProvider)
                                    .build();
                        }else {
                            chatClient = ChatClient.builder(zhiPuAiChatModel)
                                    .defaultSystem(role)
                                    .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                    .defaultToolCallbacks(toolCallbackProvider)
                                    .build();
                        }
                    }else {
                        if (messageDto.getIsRag()){
                            chatClient = ChatClient.builder(zhiPuAiChatModel)
                                    .defaultSystem(role)
                                    .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                                            .searchRequest(SearchRequest.builder()
                                                    .topK(5)//最多 5 个检索结果
                                                    .similarityThreshold(0.6)//最小相似度匹配百分比
                                                    .build()).build())
                                    .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                    .build();
                        }else {
                            chatClient = ChatClient.builder(zhiPuAiChatModel)
                                    .defaultSystem(role)
                                    .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                    .build();
                        }
                    }
                    StringBuilder fullContent = new StringBuilder();
                    Flux<String> content = chatClient.prompt()
                            .user(messageDto.getMessage())
                            .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, messageDto.getChatId()))
                            .stream()
                            .content()
                            .doOnNext(fragment -> fullContent.append(fragment))
                            .doOnComplete(() -> {
                                String completeContent = fullContent.toString();
                                springAiChatMemoryMapper.insert(new SpringAiChatMemory()
                                        .setContent(messageDto.getMessage())
                                        .setType("USER")
                                        .setModel(AiModelConstant.GLM)
                                        .setConversationId(messageDto.getChatId())
                                        .setTimestamp(LocalDateTime.now()));
                                springAiChatMemoryMapper.insert(new SpringAiChatMemory()
                                        .setContent(completeContent)
                                        .setModel(AiModelConstant.GLM)
                                        .setType("ASSISTANT")
                                        .setConversationId(messageDto.getChatId())
                                        .setTimestamp(LocalDateTime.now()));
                            });

                    return content;
                }else {

                    MediaType mediaType = MediaType.valueOf(Objects.requireNonNull(messageDto.getFile().getContentType()));
                    String type = mediaType.getType();
                    boolean isImage = "image".equals(type);
                    String originalFilename = messageDto.getFile().getOriginalFilename();
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                    final String fileName = UUID.randomUUID() + "."+fileExtension;
                    String url = domin+aliUploadUtils.uploadFile(messageDto.getFile(), "chat", fileName, isImage);
                    Media media = new Media(mediaType, URI.create(url));
//                    if (isImage){
//                        fileContent ="<picture>"+url+"</picture>";
//                    }else {
//                        fileContent ="<file>"+url+"</file>";
//                    }

                    ZhiPuAiChatOptions zhiPuAiChatOptions = ZhiPuAiChatOptions.builder()
                            .model(AiModelConstant.GLM)
                            .build();
                    List<Message> messages = getMessages(messageDto.getChatId());
                    UserMessage userMessage = UserMessage.builder().media(media).text(messageDto.getMessage()).build();
                    messages.add(userMessage);
                    Flux<ChatResponse> chatResponseFlux = zhiPuAiChatModel.stream(new Prompt(messages, zhiPuAiChatOptions));
                    Flux<String> map = chatResponseFlux.mapNotNull(chatResponse ->
                                    chatResponse.getResult()!=null?
                                            chatResponse.getResult().getOutput().getText():null)
                            .mapNotNull(text -> text!=null?text:"");
                    map.doOnNext(chunk -> {
                    }).reduce((s1, s2) -> s1 + s2).subscribe(
                            result -> {
                                // 流式结束后，
                                springAiChatMemoryMapper.insert(new SpringAiChatMemory()
                                        .setContent(messageDto.getMessage())
                                        .setType("USER")
                                        .setMedia(url)
                                        .setModel(AiModelConstant.GLM)
                                        .setIsMcp(messageDto.getIsMcp())
                                        .setIsRag(messageDto.getIsRag())
                                        .setConversationId(messageDto.getChatId())
                                        .setTimestamp(LocalDateTime.now()));
                                springAiChatMemoryMapper.insert(new SpringAiChatMemory()
                                        .setContent(result)
                                        .setType("ASSISTANT")
                                        .setIsMcp(messageDto.getIsMcp())
                                        .setIsRag(messageDto.getIsRag())
                                        .setModel(AiModelConstant.GLM)
                                        .setConversationId(messageDto.getChatId())
                                        .setTimestamp(LocalDateTime.now()));
                            }
                    );
                    return map;
                }
            }
        }
        return Flux.empty();
    }

    /**
     * 无文件聊天
     * @param messageDto messageDto
     * @param model 模型
     * @param modelName 模型名称
     * @return Flux<String>
     */
    public Flux<String> ChatWithoutFile(MessageDto messageDto,OpenAiChatModel model,String modelName,String role) {
        ChatClient chatClient;
        //是否开启MCP
        if (messageDto.getIsMcp()){
            //是否开启了RAG
            if (messageDto.getIsRag()){
                chatClient = ChatClient.builder(model)
                        .defaultSystem(role)
                        .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(5)//最多 5 个检索结果
                                        .similarityThreshold(0.6)//最小相似度匹配百分比
                                        .build()).build())
                        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .defaultToolCallbacks(toolCallbackProvider)
                        .build();
            }else {
                chatClient = ChatClient.builder(model)
                        .defaultSystem(role)
                        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .defaultToolCallbacks(toolCallbackProvider)
                        .build();
            }
        }else {
            if (messageDto.getIsRag()){
                chatClient = ChatClient.builder(model)
                        .defaultSystem(role)
                        .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(5)//最多 5 个检索结果
                                        .similarityThreshold(0.6)//最小相似度匹配百分比
                                        .build()).build())
                        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .build();
            }else {
                chatClient = ChatClient.builder(model)
                        .defaultSystem(role)
                        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .build();
            }
        }

        StringBuilder fullContent = new StringBuilder();
        Flux<String> content = chatClient.prompt()
                .user(messageDto.getMessage())
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, messageDto.getChatId()))
                .stream()
                .content()
                .doOnNext(fragment -> fullContent.append(fragment))
                .doOnComplete(() -> {
                    String completeContent = fullContent.toString();
                    springAiChatMemoryMapper.insert(new SpringAiChatMemory()
                            .setContent(messageDto.getMessage())
                            .setType("USER")
                            .setModel(modelName)
                            .setIsMcp(messageDto.getIsMcp())
                            .setIsRag(messageDto.getIsRag())
                            .setConversationId(messageDto.getChatId())
                            .setTimestamp(LocalDateTime.now()));
                    springAiChatMemoryMapper.insert(new SpringAiChatMemory()
                            .setContent(completeContent)
                            .setModel(modelName)
                            .setType("ASSISTANT")
                            .setIsMcp(messageDto.getIsMcp())
                            .setIsRag(messageDto.getIsRag())
                            .setConversationId(messageDto.getChatId())
                            .setTimestamp(LocalDateTime.now()));
                });

        return content;
    }

    /**
     * 文件聊天
     * @param messageDto messageDto
     * @param model 模型
     * @param modelName 模型名称
     * @return Flux<String>
     */
    public Flux<String> ChatWithFile(MessageDto messageDto,OpenAiChatModel model,String modelName) {
        String fileContent = "";
        MediaType mediaType = MediaType.valueOf(Objects.requireNonNull(messageDto.getFile().getContentType()));
        String type = mediaType.getType();
        boolean isImage = "image".equals(type);
        String originalFilename = messageDto.getFile().getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        final String fileName = UUID.randomUUID() + "."+fileExtension;
        String url = domin+aliUploadUtils.uploadFile(messageDto.getFile(), "chat", fileName, isImage);
        Media media = new Media(mediaType, URI.create(url));
//        if (isImage){
//            fileContent ="<picture>"+url+"</picture>";
//        }else {
//            fileContent ="<file>"+url+"</file>";
//        }

        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(modelName)
                .build();
        List<Message> messages = getMessages(messageDto.getChatId());
        UserMessage userMessage = UserMessage.builder().media(media).text(messageDto.getMessage()).build();
        messages.add(userMessage);
        Flux<ChatResponse> chatResponseFlux =model.stream(new Prompt(messages, openAiChatOptions));
        Flux<String> map = chatResponseFlux.mapNotNull(chatResponse ->
                        chatResponse.getResult()!=null?
                                chatResponse.getResult().getOutput().getText():null)
                .mapNotNull(text -> text!=null?text:"");
        String finalFileContent = fileContent;
        map.doOnNext(chunk -> {
        }).reduce((s1, s2) -> s1 + s2).subscribe(
                result -> {
                    // 流式结束后，
                    springAiChatMemoryMapper.insert(new SpringAiChatMemory()
                            .setContent(messageDto.getMessage())
                            .setType("USER")
                            .setMedia(finalFileContent)
                            .setIsMcp(messageDto.getIsMcp())
                            .setIsRag(messageDto.getIsRag())
                            .setModel(modelName)
                            .setConversationId(messageDto.getChatId())
                            .setTimestamp(LocalDateTime.now()));
                    springAiChatMemoryMapper.insert(new SpringAiChatMemory()
                            .setContent(result)
                            .setType("ASSISTANT")
                            .setIsMcp(messageDto.getIsMcp())
                            .setIsRag(messageDto.getIsRag())
                            .setModel(modelName)
                            .setConversationId(messageDto.getChatId())
                            .setTimestamp(LocalDateTime.now()));
                }
        );
        return map;

    }

    public List<Message> getMessages(String conversationId) {
        List<SpringAiChatMemory> springAiChatMemories = springAiChatMemoryMapper.selectList(new QueryWrapper<SpringAiChatMemory>().lambda()
                .eq(SpringAiChatMemory::getConversationId, conversationId)
                .orderByDesc(SpringAiChatMemory::getId)
                .last("LIMIT 12"));
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
}
