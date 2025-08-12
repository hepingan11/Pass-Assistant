package top.hepingan.pasystemservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.hepingan.pacommon.constants.ServerConstant;
import top.hepingan.pacommon.dto.AnnouncementDto;
import top.hepingan.pacommon.dto.PutExchangeDto;
import top.hepingan.pacommon.dto.ServerConfigDto;
import top.hepingan.pacommon.dto.TerminalConfigDto;
import top.hepingan.pacommon.entity.Exchange;
import top.hepingan.pacommon.mapper.ExchangeMapper;
import top.hepingan.pacommon.structure.AnnouncementStructure;
import top.hepingan.pacommon.structure.ControlStructure;
import top.hepingan.pacommon.utils.BeanUtils;
import top.hepingan.pacommon.utils.RedisUtils;
import top.hepingan.pacommon.utils.StringUtils;
import top.hepingan.pacommon.vo.DispositionVo;
import top.hepingan.pacommon.vo.admin.RedemptionCodeVo;
import top.hepingan.pasystemservice.common.ChatGptCommon;
import top.hepingan.pasystemservice.common.UserInspiritCommon;
import top.hepingan.pasystemservice.service.ServerService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
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
                        .setVideoFrequency(dto.getVideoFrequency())
        );


        redisUtils.setValue(ServerConstant.SD_BUTTON, dto.getSdButton());
        redisUtils.setValue(ServerConstant.Dialogue_Storage, dto.getDialogueStorage());
        //Link Top Img
        redisUtils.setValue(ServerConstant.LINK_TOP_IMG, dto.getLinkTopImg());
        redisUtils.setValue(ServerConstant.IS_HADOOP, dto.getIsHadoop());
        // CHAT GPT
        redisUtils.setValue(ServerConstant.CHAT_GPT_CONFIG,
                new ChatGptCommon.ChatGptStructure()
                        .setGptFrequency(dto.getGptFrequency())
                        .setOpenKey(dto.getOpenKey())
                        .setOpenAiUrl(dto.getOpenAiUrl())
                        .setDeepseekKey(dto.getDeepseekKey())
                        .setDoubaoKey(dto.getDoubaoKey())
                        .setClaudeKey(dto.getClaudeKey())
                        .setGlmKey(dto.getGlmKey())
                        .setGptTextImageFrequency(dto.getGptTextImageFrequency())
        );
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
        final String isHadoop = String.valueOf(redisUtils.getValue(ServerConstant.IS_HADOOP));
        final String sdButton = String.valueOf(redisUtils.getValue(ServerConstant.SD_BUTTON));
        final String dialogueStorage = String.valueOf(redisUtils.getValue(ServerConstant.Dialogue_Storage));

        dispositionVo.setGlmKey(chatGptStructure != null ? chatGptStructure.getGlmKey() : null);
        dispositionVo.setGptFrequency(chatGptStructure != null ? chatGptStructure.getGptFrequency() : null);
        dispositionVo.setGptPlusFrequency(chatGptStructure != null ? chatGptStructure.getGptPlusFrequency() : null);
        dispositionVo.setOpenAiUrl(chatGptStructure != null ? chatGptStructure.getOpenAiUrl() : null);
        dispositionVo.setOpenKey(chatGptStructure != null ? chatGptStructure.getOpenKey() : null);
        dispositionVo.setDeepseekKey(chatGptStructure != null ? chatGptStructure.getDeepseekKey() : null);
        dispositionVo.setDoubaoKey(chatGptStructure !=null ? chatGptStructure.getDoubaoKey() : null);
        dispositionVo.setClaudeKey(chatGptStructure !=null ? chatGptStructure.getClaudeKey() : null);
        dispositionVo.setGptTextImageFrequency(chatGptStructure != null ? chatGptStructure.getGptTextImageFrequency() : null);
        dispositionVo.setLinkTopImg(StringUtils.isNotBlank(linkTopImg) ? linkTopImg : "");
        dispositionVo.setIsHadoop(StringUtils.isNotBlank(isHadoop) ? isHadoop : "");
        dispositionVo.setSdButton(StringUtils.isNotBlank(sdButton) ? sdButton : "");
        dispositionVo.setDialogueStorage(StringUtils.isNotBlank(dialogueStorage) ? dialogueStorage : "");
        dispositionVo.setSignInFrequency(inspiritStructure != null ? inspiritStructure.getSignInFrequency() : null);
        dispositionVo.setVideoFrequency(inspiritStructure != null ? inspiritStructure.getVideoFrequency() : null);
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



}
