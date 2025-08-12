package top.hepingan.paaiservice.common;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.hepingan.paaiservice.config.ChatGptDefaultConfig;
import top.hepingan.pacommon.constants.ServerConstant;
import top.hepingan.pacommon.utils.RedisUtils;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatGptCommon {

    private final RedisUtils redisUtils;

    private final ChatGptDefaultConfig chatGptDefaultConfig;

    public ChatGptStructure getChatGptStructure() {
        final Object value = redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        if (value == null) {
            return getDefault();
        }
        try {
            return (ChatGptStructure) value;
        } catch (Exception e) {
            log.warn("已清除上一个版本的GPT配置,请前往控制台重新配置ChatGPT参数配置");
            redisUtils.delKey(ServerConstant.CHAT_GPT_CONFIG);
            return getDefault();
        }
    }

    private ChatGptStructure getDefault() {
        log.warn("请前往控制台配置ChatGPT参数配置");
        return new ChatGptStructure()
                .setOpenAiUrl(chatGptDefaultConfig.getOpenAiUrl())
                .setGptFrequency(chatGptDefaultConfig.getGptFrequency())
                .setGptTextImageFrequency(chatGptDefaultConfig.getGptTextImageFrequency())
                .setGptPlusFrequency(chatGptDefaultConfig.getGptPlusFrequency())
                .setGlmKey(chatGptDefaultConfig.getGlmKey())
                .setDeepseekKey(chatGptDefaultConfig.getDeepseekKey())
                .setDoubaoKey(chatGptDefaultConfig.getDoubaoKey())
                .setOpenKey(chatGptDefaultConfig.getOpenKey());
    }

    @Data
    @Accessors(chain = true)
    public static class ChatGptStructure {

        private String openAiUrl;

        private String openKey;

        private String openAiPlusUrl;

        private String openPlusKey;

        private String deepseekKey;

        private String doubaoKey;

        private String claudeKey;

        private String glmKey;

        private Long gptFrequency;

        private Long gptPlusFrequency;

        private Long gptTextImageFrequency;
    }

}
