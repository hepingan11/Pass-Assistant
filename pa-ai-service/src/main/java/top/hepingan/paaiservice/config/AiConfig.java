package top.hepingan.paaiservice.config;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import top.hepingan.paaiservice.common.ChatGptCommon;
import top.hepingan.paaiservice.common.MysqlChatMemory;
import top.hepingan.pacommon.constants.AiBaseUrlConstant;
import top.hepingan.pacommon.constants.AiModelConstant;
import top.hepingan.pacommon.constants.ServerConstant;
import top.hepingan.pacommon.utils.RedisUtils;


@Configuration
public class AiConfig {


    @jakarta.annotation.Resource
    private RedisUtils redisUtils;

    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    public VectorStore vectorStore(ZhiPuAiEmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

//    @Bean
//    public ChatMemory chatMemory(JdbcChatMemoryRepository chatMemoryRepository){
//        return MessageWindowChatMemory.builder()
//                .chatMemoryRepository(chatMemoryRepository)
//                .maxMessages(15)
//                .build();
//    }

    @Bean
    @Primary
    public ChatClient openaiClient(OpenAiChatModel model, MysqlChatMemory chatMemory) {
        return ChatClient.builder(model)
                .defaultSystem("你叫何平安，是一个高冷酷酷的IT高手")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean
    public ChatClient zhipuClient(ZhiPuAiChatModel model, MysqlChatMemory chatMemory) {
        return ChatClient.builder(model)
                .defaultSystem("你叫何平安，是一个高冷酷酷的IT高手")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }


    @Bean
    public OpenAiChatModel deepseekModel(){
        final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(chatGptStructure.getDeepseekKey())
                        .baseUrl(AiBaseUrlConstant.DEEPSEEK)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder().model(AiModelConstant.DEEPSEEK).build())
                .build();
    }

    @Bean
    @Primary
    public OpenAiChatModel openaiModel(){
        final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(chatGptStructure.getOpenKey())
                        .baseUrl(AiBaseUrlConstant.GPT)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder().model("gpt-5").build())
                .build();
    }

    @Bean
    public OpenAiChatModel claudeModel(){
        final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(chatGptStructure.getClaudeKey())
                        .baseUrl(AiBaseUrlConstant.CLAUDE)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder().model(AiModelConstant.CLAUDE).build())
                .build();
    }

    @Bean
    public OpenAiChatModel qwenModel(){
        final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(chatGptStructure.getTongyiKey())
                        .baseUrl(AiBaseUrlConstant.QWEN)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder().model(AiModelConstant.QWEN).build())
                .build();
    }

    @Bean
    public OpenAiChatModel geminiModel(){
        final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(chatGptStructure.getClaudeKey())
                        .baseUrl(AiBaseUrlConstant.GEMINI)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder().model(AiModelConstant.GEMINI).build())
                .build();
    }

    @Bean
    public OpenAiChatModel gorkModel(){
        final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(chatGptStructure.getClaudeKey())
                        .baseUrl(AiBaseUrlConstant.GROK)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder().model(AiModelConstant.GROK).build())
                .build();
    }

    @Bean
    public OpenAiChatModel doubaoModel(){
        final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(chatGptStructure.getClaudeKey())
                        .baseUrl(AiBaseUrlConstant.DOUBAO)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder().model(AiModelConstant.DOUBAO).build())
                .build();
    }


    @Bean
    CommandLineRunner commandLineRunner(@Autowired VectorStore vectorStore, @Value("classpath:templates/test.txt") Resource testTxt){
        return args -> {
            vectorStore.write(new TokenTextSplitter().transform(new TextReader(testTxt).read()));
        };
    }


}
