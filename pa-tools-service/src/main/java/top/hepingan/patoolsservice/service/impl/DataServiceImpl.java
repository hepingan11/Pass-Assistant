package top.hepingan.patoolsservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.hepingan.pacommon.entity.ConversationUser;
import top.hepingan.pacommon.entity.SpringAiChatMemory;
import top.hepingan.pacommon.mapper.ConversationUserMapper;
import top.hepingan.pacommon.mapper.SpringAiChatMemoryMapper;
import top.hepingan.pacommon.utils.UserUtils;
import top.hepingan.patoolsservice.service.DataService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataServiceImpl implements DataService {

    private final SpringAiChatMemoryMapper springAiChatMemoryMapper;

    private final ConversationUserMapper conversationUserMapper;


    @Override
    public List<Map<String,Integer>> timeFrequency(Integer type) {
        List<ConversationUser> conversationUsers = conversationUserMapper.selectList(new QueryWrapper<ConversationUser>().lambda()
                .eq(ConversationUser::getUserId, UserUtils.getCurrentLoginId()));
        Map<String, Integer> dateCountMap = new HashMap<>();
        if (type<4){
            for (ConversationUser conversationUser : conversationUsers){
                List<SpringAiChatMemory> springAiChatMemories = springAiChatMemoryMapper.selectList(new QueryWrapper<SpringAiChatMemory>().lambda()
                        .ge(SpringAiChatMemory::getTimestamp, LocalDateTime.now().minusMonths(type))
                        .eq(SpringAiChatMemory::getType, "USER")
                        .eq(SpringAiChatMemory::getConversationId, conversationUser.getConversationId()));
                for (SpringAiChatMemory springAiChatMemory : springAiChatMemories){
                    // 将时间戳格式化为日期字符串（精确到日）
                    String dateStr = springAiChatMemory.getTimestamp().toLocalDate().toString();
                    // 统计相同日期的数据个数
                    dateCountMap.put(dateStr, dateCountMap.getOrDefault(dateStr, 0) + 1);
                }
            }
        }else {
            for (ConversationUser conversationUser : conversationUsers){
                List<SpringAiChatMemory> springAiChatMemories = springAiChatMemoryMapper.selectList(new QueryWrapper<SpringAiChatMemory>().lambda()
                        .ge(SpringAiChatMemory::getTimestamp, LocalDateTime.now().minusMonths(type))
                        .eq(SpringAiChatMemory::getConversationId, conversationUser.getConversationId()));
                for (SpringAiChatMemory springAiChatMemory : springAiChatMemories){
                    // 将时间戳格式化为日期字符串（精确到日）
                    String dateStr = springAiChatMemory.getTimestamp().getYear() + "-" + String.format("%02d", springAiChatMemory.getTimestamp().getMonthValue());                    // 统计相同日期的数据个数
                    dateCountMap.put(dateStr, dateCountMap.getOrDefault(dateStr, 0) + 1);
                }
            }
        }

        List<Map<String, Integer>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dateCountMap.entrySet()) {
            Map<String, Integer> item = new HashMap<>();
            item.put(entry.getKey(), entry.getValue());
            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getWordFrequency(int v) {
        String text = "";
        //mysql
        List<ConversationUser> conversationUsers = conversationUserMapper.selectList(new QueryWrapper<ConversationUser>().lambda()
                .eq(ConversationUser::getUserId, UserUtils.getCurrentLoginId()));
        if (v == 1){
            for (ConversationUser conversationUser : conversationUsers) {
                List<SpringAiChatMemory> springAiChatMemories = springAiChatMemoryMapper.selectList(new QueryWrapper<SpringAiChatMemory>().lambda()
                        .eq(SpringAiChatMemory::getConversationId, conversationUser.getConversationId())
                        .eq(SpringAiChatMemory::getType, "USER"));
                for (SpringAiChatMemory springAiChatMemory : springAiChatMemories) {
                    StringBuilder sb = new StringBuilder(text);
                    sb.append(springAiChatMemory.getContent());
                    text = sb.toString();
                }
            }
        }else {
            for (ConversationUser conversationUser : conversationUsers) {
                List<SpringAiChatMemory> springAiChatMemories = springAiChatMemoryMapper.selectList(new QueryWrapper<SpringAiChatMemory>().lambda()
                        .eq(SpringAiChatMemory::getConversationId, conversationUser.getConversationId())
                        .eq(SpringAiChatMemory::getType, "ASSISTANT"));
                for (SpringAiChatMemory springAiChatMemory : springAiChatMemories) {
                    StringBuilder sb = new StringBuilder(text);
                    sb.append(springAiChatMemory.getContent());
                    text = sb.toString();
                }
            }
        }

        // 提取频率最高的几个词
        List<Map.Entry<String, Integer>> list =  findTopFrequentWords(text, 20);
        ArrayList<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", list.get(i).getKey());
            item.put("value", list.get(i).getValue());
            dataList.add(item);
        }

        return dataList;
    }

    @Override
    public List<Map<String, Integer>> getModelFrequency() {
        List<ConversationUser> conversationUsers = conversationUserMapper.selectList(new QueryWrapper<ConversationUser>().lambda()
                .eq(ConversationUser::getUserId, UserUtils.getCurrentLoginId()));

        Map<String, Integer> modelCountMap = new HashMap<>();
        for (ConversationUser conversationUser : conversationUsers){
            List<SpringAiChatMemory> springAiChatMemories = springAiChatMemoryMapper.selectList(new QueryWrapper<SpringAiChatMemory>().lambda()
                    .eq(SpringAiChatMemory::getConversationId, conversationUser.getConversationId())
                    .eq(SpringAiChatMemory::getType, "USER"));
            for (SpringAiChatMemory springAiChatMemory : springAiChatMemories){
                if (springAiChatMemory.getModel() != null){
                    modelCountMap.put(springAiChatMemory.getModel() , modelCountMap.getOrDefault(springAiChatMemory.getModel(), 0) + 1);
                }
            }
        }
        List<Map<String, Integer>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : modelCountMap.entrySet()) {
            Map<String, Integer> item = new HashMap<>();
            item.put(entry.getKey(), entry.getValue());
            result.add(item);
        }

        return result;
    }

    public static List<Map.Entry<String, Integer>> findTopFrequentWords(String text, int n) {
            if (text == null || text.isEmpty()) return List.of();

            List<Term> terms = HanLP.segment(text);
            Map<String, Integer> cnt = new HashMap<>();

            for (Term t : terms) {
                String w = t.word.trim();
                String pos = String.valueOf(t.nature); // 词性
                if (w.length() < 2) continue;          // 常见做法：忽略单字词，可按需调整
                // 仅统计名词/动词/形容词，可按需放宽
                if (!(pos.startsWith("n") || pos.startsWith("v") || pos.startsWith("a"))) continue;
                cnt.merge(w, 1, Integer::sum);
            }

            return cnt.entrySet().stream()
                    .sorted((a, b) -> {
                        int c = Integer.compare(b.getValue(), a.getValue());
                        return c != 0 ? c : a.getKey().compareTo(b.getKey());
                    })
                    .limit(n > 0 ? n : Long.MAX_VALUE)
                    .collect(Collectors.toList());

    }
}
