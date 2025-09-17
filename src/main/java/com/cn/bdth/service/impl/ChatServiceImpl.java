package com.cn.bdth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.bdth.common.MysqlChatMemory;
import com.cn.bdth.config.AiConfig;
import com.cn.bdth.config.McpListConfig;
import com.cn.bdth.constants.AiModelConstant;
import com.cn.bdth.dto.MessageDto;
import com.cn.bdth.entity.ConversationUser;
import com.cn.bdth.entity.Mcps;
import com.cn.bdth.entity.Rag;
import com.cn.bdth.entity.SpringAiChatMemory;
import com.cn.bdth.mapper.ConversationUserMapper;
import com.cn.bdth.mapper.McpsMapper;
import com.cn.bdth.mapper.RagMapper;
import com.cn.bdth.mapper.SpringAiChatMemoryMapper;
import com.cn.bdth.service.ChatService;
import com.cn.bdth.utils.AliUploadUtils;
import com.cn.bdth.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final SpringAiChatMemoryMapper springAiChatMemoryMapper;

    private final ConversationUserMapper conversationUserMapper;

    private final VectorStore vectorStore;

    private final McpListConfig mcpListConfig;

    private final ZhiPuAiChatModel zhiPuAiChatModel;

    private final AliUploadUtils aliUploadUtils;

    private final OpenAiChatModel openaiModel;

    private final MysqlChatMemory chatMemory;

    private final AiConfig aiConfig;

    private final RagMapper ragMapper;

    private final McpsMapper mcpsMapper;


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
                .orderByDesc(ConversationUser::getUpdatedTime));
    }


    @Override
    public Flux<String> aiChat(MessageDto messageDto) throws MalformedURLException {
        ConversationUser conversationUser = conversationUserMapper.selectOne(new QueryWrapper<ConversationUser>().lambda()
                .eq(ConversationUser::getConversationId, messageDto.getChatId()));
        Long currentLoginId = UserUtils.getCurrentLoginId();

        if (conversationUser == null){
            conversationUserMapper.insert(new ConversationUser()
                    .setConversationId(messageDto.getChatId())
                    .setCreatedTime(LocalDateTime.now())
                            .setUpdatedTime(LocalDateTime.now())
                    .setTitle(LocalDateTime.now().toString().substring(5,10)+"新对话")
                    .setUserId(currentLoginId));
        }


        if (messageDto.getIsRag()){
            List<Rag> rags = ragMapper.selectList(new QueryWrapper<Rag>().lambda()
                    .eq(Rag::getUserId, currentLoginId).eq(Rag::getIsEnable, true));
            if (rags != null && !rags.isEmpty()){
                for (Rag rag : rags){
                    Resource resource = new UrlResource(domin+rag.getRagUrl());
                    vectorStore.write(new TokenTextSplitter().transform(new TextReader(resource).read()));
                }
            }
        }

        //添加mcp
        List<ToolCallback> toolCallbacksFromAsyncClients = new ArrayList<>();
        if (messageDto.getMcpList() != null && messageDto.getIsMcp()){
            for (Long mcpId : messageDto.getMcpList()){
                String methodName = mcpsMapper.selectById(mcpId).getMethodName();
                List<ToolCallback> toolCallbackByName = mcpListConfig.getToolCallbackByName(methodName);
                if (toolCallbackByName != null) {
                    toolCallbacksFromAsyncClients.addAll(toolCallbackByName);
                }
            }
        }
        ToolCallbackProvider toolCallbackProvider =ToolCallbackProvider.from(toolCallbacksFromAsyncClients);


        // 如果messageDto中的role字段为null，则设置默认角色描述；否则使用传入的role值
        String role="你名字叫何平安，是一个高冷酷酷的IT高手";
        if (conversationUser != null) {
            if (conversationUser.getRole() != null && !conversationUser.getRole().isEmpty()){
                role = conversationUser.getRole();
            }
        }

        if (messageDto.getModel() ==null || messageDto.getModel().isEmpty()){
            messageDto.setModel("GLM");
        }

        if (conversationUser != null) {
            conversationUserMapper.update(conversationUser.setUpdatedTime(LocalDateTime.now()),new QueryWrapper<ConversationUser>().lambda()
                    .eq(ConversationUser::getConversationId, messageDto.getChatId()));
        }


        switch (messageDto.getModel()) {
            case "GPT"->{
                if (messageDto.getFile() == null){
                    return ChatWithoutFile(messageDto, openaiModel, AiModelConstant.GPT,role,toolCallbackProvider);
                }else {
                    return ChatWithFile(messageDto, openaiModel, AiModelConstant.GPT, role);
                }
            }
            case "CLAUDE" ->{
                OpenAiChatModel claudeModel = aiConfig.claudeModel();
                if (messageDto.getFile() == null){
                    return ChatWithoutFile(messageDto, claudeModel, AiModelConstant.CLAUDE,role,toolCallbackProvider);
                }else {
                    return ChatWithFile(messageDto, claudeModel, AiModelConstant.CLAUDE,role);
                }
            }
            case "DEEPSEEK" -> {
                return ChatWithoutFile(messageDto, aiConfig.deepseekModel(),AiModelConstant.DEEPSEEK,role,toolCallbackProvider);
            }
            case "QWEN" ->{
                return ChatWithoutFile(messageDto,aiConfig.qwenModel(),AiModelConstant.QWEN,role,toolCallbackProvider);
            }
            case "GEMINI" -> {
                return ChatWithoutFile(messageDto,aiConfig.geminiModel(),AiModelConstant.GEMINI,role,toolCallbackProvider);
            }
            case "GROK" ->{
                return ChatWithoutFile(messageDto,aiConfig.gorkModel(),AiModelConstant.GROK,role,toolCallbackProvider);
            }
            case "DEEPSEEK_R"->{
                return ChatWithoutFile(messageDto,aiConfig.deepseekModel(),AiModelConstant.DEEPSEEK_R,role,toolCallbackProvider);
            }
            case "DOUBAO" ->{
                OpenAiChatModel doubaoModel = aiConfig.doubaoModel();
                if (messageDto.getFile() == null){
                    return ChatWithoutFile(messageDto, doubaoModel, AiModelConstant.DOUBAO,role,toolCallbackProvider);
                }else {
                    return ChatWithFile(messageDto, doubaoModel, AiModelConstant.DOUBAO,role);
                }
            }
            case "GLM" ->{
                if (messageDto.getFile() == null){
                    return ChatWithoutFile(messageDto, zhiPuAiChatModel, AiModelConstant.GLM,role,toolCallbackProvider);
                }else {
                    return ChatWithFile(messageDto, zhiPuAiChatModel, AiModelConstant.GLM,role);
                }
            }
        }

        return Flux.empty();
    }

    @Override
    public void updateRag(MultipartFile file) {
        List<Rag> rags = ragMapper.selectList(new QueryWrapper<Rag>().lambda().eq(Rag::getUserId, UserUtils.getCurrentLoginId()));
        if (rags.size() == 3){
            throw new RuntimeException("最多只能添加3个知识库");
        }
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        final String fileName = UUID.randomUUID() + "."+fileExtension;
        String url = aliUploadUtils.uploadFile(file, "chat", fileName, false);
        ragMapper.insert(new Rag().setRagName(originalFilename)
                .setRagUrl( url)
                .setIsEnable(true)
                .setUserId(UserUtils.getCurrentLoginId()));
    }

    @Override
    public List<Mcps> mcpList() {
        return mcpsMapper.selectList(null);
    }

    @Override
    public List<Rag> ragList() {
        return ragMapper.selectList(new QueryWrapper<Rag>().lambda()
                .eq(Rag::getUserId, UserUtils.getCurrentLoginId()));
    }

    @Override
    public void deleteRag(Long ragId) {
        aliUploadUtils.deleteFile(ragMapper.selectById(ragId).getRagUrl());
        ragMapper.deleteById(ragId);
    }

    @Override
    public void toggleRag(Long ragId) {
        ragMapper.updateById(new Rag().setRagId(ragId).setIsEnable(!ragMapper.selectById(ragId).getIsEnable()));
    }

    @Override
    public Flux<String> aiChat2(MessageDto messageDto) {
        return ChatClient.builder(zhiPuAiChatModel)
                .defaultSystem("你是一个乐于帮助解决问题的小助手")
                .build().prompt()
                .user(messageDto.getMessage())
                .stream()
                .content();
    }

    /**
     * 无文件聊天
     * @param messageDto messageDto
     * @param model 模型
     * @param modelName 模型名称
     * @return Flux<String>
     */
    public Flux<String> ChatWithoutFile(MessageDto messageDto,ChatModel model,String modelName,String role,ToolCallbackProvider toolCallbackProvider) {
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
    public Flux<String> ChatWithFile(MessageDto messageDto, ChatModel model, String modelName,String role) {
        MediaType mediaType = MediaType.valueOf(Objects.requireNonNull(messageDto.getFile().getContentType()));
        String type = mediaType.getType();
        boolean isImage = "image".equals(type);
        String originalFilename = messageDto.getFile().getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        final String fileName = UUID.randomUUID() + "."+fileExtension;
        String url = domin+aliUploadUtils.uploadFile(messageDto.getFile(), "chat", fileName, isImage);
        Media media = new Media(mediaType, URI.create(url));


        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(modelName)
                .build();
        SystemMessage systemMessage = new SystemMessage(role);
        List<Message> messages = getMessages(messageDto.getChatId());
        messages.add(0,systemMessage);
        UserMessage userMessage = UserMessage.builder().media(media).text(messageDto.getMessage()).build();
        messages.add(userMessage);
        Flux<ChatResponse> chatResponseFlux =model.stream(new Prompt(messages, openAiChatOptions));
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
                .orderByDesc(SpringAiChatMemory::getTimestamp)
                .last("LIMIT 14"));
        List<Message> messages = new ArrayList<>();
        Collections.reverse(springAiChatMemories);
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

