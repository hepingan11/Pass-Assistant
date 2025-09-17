package com.cn.bdth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.bdth.common.ChatGptCommon;
import com.cn.bdth.constants.MediaModelConstant;
import com.cn.bdth.constants.ServerConstant;
import com.cn.bdth.dto.ZhipuDrawDto;
import com.cn.bdth.entity.Drawing;
import com.cn.bdth.entity.User;
import com.cn.bdth.enums.FileEnum;
import com.cn.bdth.mapper.DrawingMapper;
import com.cn.bdth.mapper.UserMapper;
import com.cn.bdth.service.DrawService;
import com.cn.bdth.utils.AliUploadUtils;
import com.cn.bdth.utils.BeanUtils;
import com.cn.bdth.utils.RedisUtils;
import com.cn.bdth.utils.UserUtils;
import com.cn.bdth.vo.admin.DrawDetailVo;
import jodd.bean.BeanUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class DrawServiceImpl implements DrawService {

    private final DrawingMapper drawingMapper;
    private final RedisUtils redisUtils;
    private final UserMapper userMapper;
    private final AliUploadUtils aliUploadUtils;

    @Value("${ali-oss.domain}")
    private String domain;

    @Data
    @Accessors(chain = true)
    private static class DoubaoDrawEditDto {
        private String model;

        private String prompt;

        private String image;

        private Boolean watermark =false;

    }

    @Data
    @Accessors(chain = true)
    private static class DoubaoDrawDto {
        private String model;

        private String prompt;

        private String size;

        private Boolean watermark =false;
    }


    @Override
    public String addZhipuDrawingTask(ZhipuDrawDto dto, MultipartFile file) {
        User user = userMapper.selectById(UserUtils.getCurrentLoginId());
        if (user.getFrequency() < 11 && !dto.getModel().equals("COGVIEW_3")) {
            throw new RuntimeException("IT币低于10，请先赞助哦~");
        }
        // 设置请求的URL地址
        CloseableHttpClient aDefault = HttpClients.createDefault();
        final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        String url ="";
        String key = "";
        DoubaoDrawEditDto seedEdit = new DoubaoDrawEditDto();
        DoubaoDrawDto seedDto = new DoubaoDrawDto();
        String imageUrl="";
        switch (dto.getModel()){
            case "DOUBAO_SEEDREAM":
                seedDto.setModel(MediaModelConstant.DOUBAO_SEEDREAM)
                        .setPrompt(dto.getPrompt())
                        .setSize(dto.getSize());
                url = MediaModelConstant.DOUBAO_URL;
                key = chatGptStructure.getDoubaoKey();
                break;
            case "COGVIEW_4":
                dto.setModel(MediaModelConstant.COGVIEW_4);
                url = MediaModelConstant.ZHIPU_URL;
                key = chatGptStructure.getGlmKey();
                break;
            case "COGVIEW_3":
                dto.setModel(MediaModelConstant.COGVIEW_3);
                url = MediaModelConstant.ZHIPU_URL;
                key = chatGptStructure.getGlmKey();
                break;
            case "DOUBAO_SEEDEDIT":
                url = MediaModelConstant.DOUBAO_URL;
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                final String fileName = UUID.randomUUID() + "."+fileExtension;
                imageUrl = aliUploadUtils.uploadFile(file, FileEnum.PAINTING.getDec(), fileName, true);
                seedEdit.setPrompt(dto.getPrompt())
                        .setImage(domain+imageUrl)
                        .setModel(MediaModelConstant.DOUBAO_SEEDEDIT);
                key = chatGptStructure.getDoubaoKey();
                break;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "Bearer " +key);
        httpPost.setHeader("Content-Type", "application/json");
        if (file != null && "DOUBAO_SEEDEDIT".equals(dto.getModel())){
            httpPost.setEntity(new StringEntity(JSON.toJSONString(seedEdit), ContentType.APPLICATION_JSON));
        }else if (dto.getModel().equals("DOUBAO_SEEDREAM")){
            httpPost.setEntity(new StringEntity(JSON.toJSONString(seedDto), ContentType.APPLICATION_JSON));
        }else {
            httpPost.setEntity(new StringEntity(JSON.toJSONString(dto), ContentType.APPLICATION_JSON));
        }

        CloseableHttpResponse execute;
        try {
            execute = aDefault.execute(httpPost);
            String str = EntityUtils.toString(execute.getEntity());
            // 解析 JSON 字符串
            JSONObject jsonObject = JSONObject.parseObject(str);
            String generateUrl = jsonObject.getJSONArray("data")
                    .getJSONObject(0)
                    .getString("url");
            final String fileName = UUID.randomUUID() + ".png";
            String v = aliUploadUtils.uploadFileFromUrl(generateUrl, FileEnum.PAINTING.getDec(), fileName);
            drawingMapper.insert(new Drawing().setPrompt(dto.getPrompt())
                    .setUserId(UserUtils.getCurrentLoginId())
                    .setIsPublic(true)
                    .setSize(dto.getSize())
                    .setImage(imageUrl)
                    .setModel(dto.getModel())
                    .setGenerateUrl(v));
            userMapper.updateById(user.setFrequency(user.getFrequency()-10));
            return generateUrl;

        } catch (IOException e) {
            e.printStackTrace();
            return "请求出错";
        }

    }

    @Override
    public List<DrawDetailVo> listPublic(Integer page) {
        Page<Drawing> p = new Page<>(page, 20);
        List<Drawing> records = drawingMapper.selectPage(p, new QueryWrapper<Drawing>().lambda()
                .orderByDesc(Drawing::getCreatedTime)
                .eq(Drawing::getIsPublic, true)).getRecords();
        List<DrawDetailVo> drawDetailVos = new ArrayList<>();
        for (Drawing drawing : records){
            DrawDetailVo drawDetailVo = BeanUtils.copyClassProperTies(drawing, DrawDetailVo.class);
            User user = userMapper.selectById(drawing.getUserId());
            drawDetailVo.setAvatar(user.getAvatar())
                    .setUserName(user.getUserName());
            drawDetailVos.add(drawDetailVo);
        }

        return drawDetailVos;

    }

    @Override
    public List<Drawing> listPrivate(Integer page) {
        Page<Drawing> p = new Page<>(page, 15);
        return drawingMapper.selectPage(p, new QueryWrapper<Drawing>().lambda()
                .orderByDesc(Drawing::getCreatedTime)
                .eq(Drawing::getUserId,UserUtils.getCurrentLoginId())).getRecords();
    }

    @Override
    public void toggle(Long drawId) {
        drawingMapper.updateById(new Drawing().setDrawingId(drawId).setIsPublic(!drawingMapper.selectById(drawId).getIsPublic()));
    }

}
