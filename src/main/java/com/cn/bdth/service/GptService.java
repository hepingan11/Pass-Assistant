package com.cn.bdth.service;

import com.cn.bdth.dto.PersonalityDto;
import com.cn.bdth.model.GptModel;
import com.cn.bdth.structure.PersonalityConfigStructure;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 雨纷纷旧故里草木深
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
public interface GptService {




    /**
     * 建立GPT 流式连接 个性
     *
     * @param messages 请求数据
     * @return string 流数据
     */
    Flux<String> concatenationGpt(List<GptModel.Messages> messages, final Long userId);

    /**
     * 获取个性配置
     */
    PersonalityConfigStructure getPersonalityConfig(Long currentLoginId);


    /**
     * 写入个性配置
     */
    void putPersonalityConfig(final PersonalityDto dto);


}
