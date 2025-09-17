package com.cn.bdth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.bdth.constants.ServerConstant;
import com.cn.bdth.constants.user.PersonalityConstant;
import com.cn.bdth.dto.PersonalityDto;
import com.cn.bdth.entity.Personality;
import com.cn.bdth.exceptions.PersonalityConfigNullException;
import com.cn.bdth.mapper.PersonalityMapper;
import com.cn.bdth.model.GptModel;
import com.cn.bdth.service.GptService;
import com.cn.bdth.structure.PersonalityConfigStructure;
import com.cn.bdth.utils.BeanUtils;
import com.cn.bdth.utils.RedisUtils;
import com.cn.bdth.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
public class GptServiceImpl implements GptService {

    private final WebClient.Builder webClient;
    
    private final RedisUtils redisUtils;

    private final PersonalityMapper personalityMapper;

    @Override
    public PersonalityConfigStructure getPersonalityConfig(Long currentLoginId) {
        if (currentLoginId == null) {
            currentLoginId = UserUtils.getCurrentLoginId();
        }

        String key = PersonalityConstant.GPT_CONFIG + currentLoginId;
        PersonalityConfigStructure personalityConfigStructure = (PersonalityConfigStructure) redisUtils.getValue(key);

        if (personalityConfigStructure == null) {
            Personality personality = personalityMapper.selectOne(new QueryWrapper<Personality>()
                    .lambda().eq(Personality::getUserId, currentLoginId)
                    .select(
                            Personality::getPersonalityId,
                            Personality::getAnswer,
                            Personality::getModel,
                            Personality::getOpenKey,
                            Personality::getMaxTokens,
                            Personality::getTemperature,
                            Personality::getSpeed,
                            Personality::getTopP,
                            Personality::getQuestions
                    )
            );
            if (personality != null) {
                personalityConfigStructure = new PersonalityConfigStructure()
                        .setAnswer(personality.getAnswer())
                        .setModel(personality.getModel())
                        .setQuestions(personality.getQuestions())
                        .setSpeed(personality.getSpeed())
                        .setMax_tokens(personality.getMaxTokens())
                        .setTemperature(personality.getTemperature())
                        .setOpenKey(personality.getOpenKey())
                        .setTop_p(personality.getTopP());
                redisUtils.setValueTimeout(key, personalityConfigStructure, 43200);
            }
        }

        return personalityConfigStructure;
    }

    @Override
    public void putPersonalityConfig(final PersonalityDto dto) {
        final Personality personality = BeanUtils.copyClassProperTies(dto, Personality.class);
        final Long currentLoginId = UserUtils.getCurrentLoginId();
        final Personality data = personalityMapper.selectOne(new QueryWrapper<Personality>()
                .lambda()
                .eq(Personality::getUserId, currentLoginId)
                .select(Personality::getPersonalityId)
        );
        personality.setUserId(currentLoginId);
        if (data != null) personalityMapper.updateById(personality.setPersonalityId(data.getPersonalityId()));
        else {
            personalityMapper.insert(personality);
        }
        redisUtils.delKey(PersonalityConstant.GPT_CONFIG + currentLoginId);

    }



    @Override
    public Flux<String> concatenationGpt(List<GptModel.Messages> messages, final Long userId) {
        //获取个性配置
        final PersonalityConfigStructure config = getPersonalityConfig(userId);
        if (config == null) {
            throw new PersonalityConfigNullException();
        }
        //装填模型参数
        return webClient.baseUrl(config.getOpenAiUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + config.getOpenKey()).build()
                .post()
                .uri(ServerConstant.GPT_DIALOGUE)
                .body(BodyInserters.fromValue(
                        //封装MODEL
                        new GptModel().setMessages(messages)
                                .setTemperature(config.getTemperature())
                                .setModel(config.getModel())
                                .setTop_p(config.getTop_p())
                                .setMax_tokens(config.getMax_tokens())))
                .retrieve()
                .bodyToFlux(String.class);
    }

}
