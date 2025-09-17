package com.cn.bdth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.bdth.common.ChatGptCommon;
import com.cn.bdth.common.UserInspiritCommon;
import com.cn.bdth.constants.ServerConstant;
import com.cn.bdth.dto.PutExchangeDto;
import com.cn.bdth.dto.ServerConfigDto;
import com.cn.bdth.dto.TerminalConfigDto;
import com.cn.bdth.dto.admin.AnnouncementDto;
import com.cn.bdth.entity.Exchange;
import com.cn.bdth.mapper.ExchangeMapper;
import com.cn.bdth.service.ServerService;
import com.cn.bdth.structure.AnnouncementStructure;
import com.cn.bdth.structure.ControlStructure;
import com.cn.bdth.utils.BeanUtils;
import com.cn.bdth.utils.RedisUtils;
import com.cn.bdth.utils.StringUtils;
import com.cn.bdth.vo.DispositionVo;
import com.cn.bdth.vo.admin.RedemptionCodeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@RequiredArgsConstructor
@Service
public class ServerServiceImpl implements ServerService {

    private final RedisUtils redisUtils;

    private final ExchangeMapper exchangeMapper;


    @Override
    public void heavyLoadDisposition(final ServerConfigDto dto) {
        //激励
        redisUtils.setValue(ServerConstant.INSPIRIT_CONFIG,
                new UserInspiritCommon.InspiritStructure()
                        .setIncentiveFrequency(dto.getIncentiveFrequency())
                        .setSignInFrequency(dto.getSignInFrequency())
        );

        //Link Top Img
        redisUtils.setValue(ServerConstant.LINK_TOP_IMG, dto.getLinkTopImg());
        // CHAT GPT
        redisUtils.setValue(ServerConstant.CHAT_GPT_CONFIG,
                new ChatGptCommon.ChatGptStructure()
                        .setGptFrequency(dto.getGptFrequency())
                        .setOpenKey(dto.getOpenKey())
                        .setDeepseekKey(dto.getDeepseekKey())
                        .setDoubaoKey(dto.getDoubaoKey())
                        .setTongyiKey(dto.getTongyiKey())
                        .setClaudeKey(dto.getClaudeKey())
                        .setGlmKey(dto.getGlmKey())
                        .setGptTextImageFrequency(dto.getGptTextImageFrequency())
        );
    }

    @Override
    public ControlStructure getTerminal() {
        return (ControlStructure) redisUtils.getValue(ServerConstant.TERMINAL_CONFIG);
    }

    @Override
    public void putTerminal(TerminalConfigDto dto) {
        final ControlStructure controlStructure = BeanUtils.copyClassProperTies(dto, ControlStructure.class);
        redisUtils.setValue(ServerConstant.TERMINAL_CONFIG, controlStructure);
    }

    @Override
    public DispositionVo getDisposition() {
        final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        final UserInspiritCommon.InspiritStructure inspiritStructure = (UserInspiritCommon.InspiritStructure) redisUtils.getValue(ServerConstant.INSPIRIT_CONFIG);
        final DispositionVo dispositionVo = new DispositionVo();

        final String linkTopImg = String.valueOf(redisUtils.getValue(ServerConstant.LINK_TOP_IMG));

        dispositionVo.setGlmKey(chatGptStructure != null ? chatGptStructure.getGlmKey() : null);
        dispositionVo.setGptFrequency(chatGptStructure != null ? chatGptStructure.getGptFrequency() : null);
        dispositionVo.setOpenKey(chatGptStructure != null ? chatGptStructure.getOpenKey() : null);
        dispositionVo.setDeepseekKey(chatGptStructure != null ? chatGptStructure.getDeepseekKey() : null);
        dispositionVo.setDoubaoKey(chatGptStructure != null ? chatGptStructure.getDoubaoKey() : null);
        dispositionVo.setTongyiKey(chatGptStructure !=null ? chatGptStructure.getTongyiKey() : null);
        dispositionVo.setClaudeKey(chatGptStructure !=null ? chatGptStructure.getClaudeKey() : null);
        dispositionVo.setGptTextImageFrequency(chatGptStructure != null ? chatGptStructure.getGptTextImageFrequency() : null);
        dispositionVo.setLinkTopImg(StringUtils.isNotBlank(linkTopImg) ? linkTopImg : "");
        dispositionVo.setSignInFrequency(inspiritStructure != null ? inspiritStructure.getSignInFrequency() : null);
        dispositionVo.setIncentiveFrequency(inspiritStructure != null ? inspiritStructure.getIncentiveFrequency() : null);

        return dispositionVo;
    }

    @Override
    public void buildRedemptionCode(final PutExchangeDto dto) {
        List<String> list = new ArrayList<>(Math.toIntExact(dto.getBuildQuantity()));
        ThreadLocalRandom random = ThreadLocalRandom.current();
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int averageCodeLength = 8;
        int charactersLength = characters.length();
        for (int i = 0; i < dto.getBuildQuantity(); i++) {
            StringBuilder randomString = new StringBuilder(averageCodeLength);
            for (int j = 0; j < averageCodeLength; j++) {
                int index = random.nextInt(charactersLength);
                randomString.append(characters.charAt(index));
            }
            list.add(randomString.toString());
        }
        list.parallelStream().forEach(p -> exchangeMapper.insert(new Exchange().setExchangeCode(p).setFrequency(dto.getBuildFrequency())));
    }

    @Override
    public IPage<RedemptionCodeVo> getRedemptionCodePage(final int pageNum, final String prompt) {

        return exchangeMapper.selectPage(new Page<>(pageNum, 15), new QueryWrapper<Exchange>()
                .lambda()
                .like(StringUtils.notEmpty(prompt), Exchange::getExchangeCode, prompt)
                .orderByDesc(Exchange::getFrequency)
        ).convert(e -> BeanUtils.copyClassProperTies(e, RedemptionCodeVo.class));
    }

    @Override
    public void deleteRedemptionCodeBasedOnTheId(final Long id) {
        exchangeMapper.deleteById(id);
    }


    @Override
    public void putAnnouncement(final AnnouncementDto dto) {
        final AnnouncementStructure structure = BeanUtils.copyClassProperTies(dto, AnnouncementStructure.class);
        //生成随机模板
        structure.setLogotypeId(UUID.randomUUID().toString());
        structure.setCreatedTime(LocalDateTime.now());
        redisUtils.setValue(ServerConstant.ANNOUNCEMENT, structure);
    }

    @Override
    public AnnouncementStructure getAnnouncement() {
        final Object value = redisUtils.getValue(ServerConstant.ANNOUNCEMENT);
        if (value != null) {
            return (AnnouncementStructure) value;
        }
        return null;
    }


}
