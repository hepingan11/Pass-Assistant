package com.cn.bdth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.bdth.common.ChatGptCommon;
import com.cn.bdth.constants.MediaModelConstant;
import com.cn.bdth.constants.ServerConstant;
import com.cn.bdth.dto.DoubaoVideoCallback;
import com.cn.bdth.dto.VideoDto;
import com.cn.bdth.entity.Drawing;
import com.cn.bdth.entity.User;
import com.cn.bdth.entity.Video;
import com.cn.bdth.entity.VideoDetail;
import com.cn.bdth.mapper.UserMapper;
import com.cn.bdth.mapper.VideoDetailMapper;
import com.cn.bdth.mapper.VideoMapper;
import com.cn.bdth.service.VideoService;
import com.cn.bdth.utils.AliUploadUtils;
import com.cn.bdth.utils.RedisUtils;
import com.cn.bdth.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final RedisUtils redisUtils;
    private final VideoMapper videoMapper;
    private final AliUploadUtils aliUploadUtils;
    private final VideoDetailMapper videoDetailMapper;
    private final UserMapper userMapper;


    @Value("${config.doubaoCallbackUrl}")
    private String doubaoCallbackUrl;

    @Value("${ali-oss.domain}")
    private String aliOssDomain;

    /**
     * 豆包ai视频生成任务创建
     * @param prompt
     * @param file
     * @return
     */
    @Override
    public String createTask(String prompt, MultipartFile file) {
        User user = userMapper.selectById(UserUtils.getCurrentLoginId());
        if (user.getFrequency()< 50){
            throw new RuntimeException("您的IT币不足,请先去赞助哦~");
        }
        record VideoTextContent(String type,String text){}
        List<Object> content = new ArrayList<>();
        content.add(new VideoTextContent("text", prompt));
        String url = "";
        if (file != null){
            record VideoImageUrlContent(String type, Map<String ,String > image_url){}
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            final String fileName = UUID.randomUUID() + "."+fileExtension;
            url = aliUploadUtils.uploadFile(file, "video", fileName, true);
            content.add(new VideoImageUrlContent("image_url", Map.of("url", aliOssDomain+url)));
        }
        record DoubaoVideo(String model,String callback_url,List<Object> content){}
        DoubaoVideo dto = new DoubaoVideo(MediaModelConstant.DOUBAO_SEEDANCE,doubaoCallbackUrl, content);
        final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
        HttpPost httpPost = new HttpPost("https://ark.cn-beijing.volces.com/api/v3/contents/generations/tasks");
        httpPost.setHeader("Authorization", "Bearer " +chatGptStructure.getDoubaoKey());
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(JSON.toJSONString(dto), ContentType.APPLICATION_JSON));
        CloseableHttpResponse execute;
        try {
            CloseableHttpClient aDefault = HttpClients.createDefault();
            execute = aDefault.execute(httpPost);
            String str = EntityUtils.toString(execute.getEntity());
            // 解析 JSON 字符串
            JSONObject jsonObject = JSONObject.parseObject(str);
            String taskId = jsonObject.getString("id");
            videoMapper.insert(new Video().setTaskId(taskId)
                    .setIsPublic(false)
                    .setPrompt(prompt)
                    .setUserId(UserUtils.getCurrentLoginId())
                    .setStatus("running")
                    .setCreatedTime(LocalDateTime.now())
                    .setImgUrl(url.isEmpty() ? null : url));
            userMapper.updateById(user.setFrequency(user.getFrequency()-50));
            return taskId;
        } catch (IOException e) {
            e.printStackTrace();
            return "请求出错";
        }
    }

    /**
     * 获取视频列表
     * @param page
     * @return
     */
    @Override
    public List<Video> listPrivate(Integer page) {
        Page<Video> p = new Page<>(page, 8);
        List<Video> records = videoMapper.selectPage(p, new QueryWrapper<Video>().lambda()
                .orderByDesc(Video::getCreatedTime)
                .eq(Video::getUserId, UserUtils.getCurrentLoginId())).getRecords();
        for (Video video : records){
            if (video.getStatus().equals("running")){
                final ChatGptCommon.ChatGptStructure chatGptStructure = (ChatGptCommon.ChatGptStructure) redisUtils.getValue(ServerConstant.CHAT_GPT_CONFIG);
                CloseableHttpClient aDefault = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet("https://ark.cn-beijing.volces.com/api/v3/contents/generations/tasks/"+video.getTaskId());
                httpGet.setHeader("Authorization", "Bearer " +chatGptStructure.getDoubaoKey());
                httpGet.setHeader("Content-Type", "application/json");
                CloseableHttpResponse execute;
                try {
                    execute = aDefault.execute(httpGet);
                    String str = EntityUtils.toString(execute.getEntity());
                    // 解析 JSON 字符串
                    JSONObject jsonObject = JSONObject.parseObject(str);
                    if (jsonObject.getString("status").equals("succeeded")){
                        String videoUrl = jsonObject.getJSONObject("content")
                                .getString("video_url");
                        String url = aliUploadUtils.uploadFileFromUrl(videoUrl, "video", video.getTaskId() + ".mp4");
                        videoMapper.updateById(video
                                .setStatus("succeeded")
                                .setIsPublic(true)
                                .setVideoUrl(url));
                        videoDetailMapper.insert(new VideoDetail().setVideoId(video.getVideoId())
                                .setRatio(jsonObject.getString("ratio"))
                                .setDuration(jsonObject.getInteger("duration"))
                                .setModel(jsonObject.getString("model"))
                                .setSeed(jsonObject.getInteger("seed"))
                                .setResolution(jsonObject.getString("resolution"))
                                .setFramespersecond(jsonObject.getInteger("framespersecond"))
                                .setCompletion_tokens(jsonObject.getJSONObject("usage").getInteger("completion_tokens"))
                        );

                    }else if (jsonObject.getString("status").equals("failed")){
                        User user = userMapper.selectById(UserUtils.getCurrentLoginId());
                        userMapper.updateById(user.setFrequency(user.getFrequency()+50));
                        videoMapper.updateById(video.setStatus("failed"));
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return records;

    }

    /**
     * 豆包回调
     * @param doubaoVideoCallback
     */
    @Override
    public void callback(DoubaoVideoCallback doubaoVideoCallback) {
        Video video = videoMapper.selectOne(new QueryWrapper<Video>().lambda().eq(Video::getTaskId, doubaoVideoCallback.getId()));
        if (doubaoVideoCallback.getStatus().equals("succeeded")){
            String url = aliUploadUtils.uploadFileFromUrl(doubaoVideoCallback.getContent().getVideo_url(), "video", doubaoVideoCallback.getId() + ".mp4");
            video.setStatus("succeeded")
                    .setVideoUrl(url)
                    .setCoverUrl(doubaoVideoCallback.getContent().getLast_frame_url())
                    .setTaskId(doubaoVideoCallback.getId());
            videoMapper.updateById(video);
            videoDetailMapper.insert(new VideoDetail().setVideoId(video.getVideoId())
                    .setRatio(doubaoVideoCallback.getRatio())
                    .setDuration(doubaoVideoCallback.getDuration())
                    .setModel(doubaoVideoCallback.getModel())
                    .setSeed(doubaoVideoCallback.getSeed())
                    .setResolution(doubaoVideoCallback.getResolution())
                    .setFramespersecond(doubaoVideoCallback.getFramespersecond())
                    .setCompletion_tokens(doubaoVideoCallback.getUsage().getCompletion_tokens()));
        }else if (doubaoVideoCallback.getStatus().equals("failed")){
            User user = userMapper.selectById(UserUtils.getCurrentLoginId());
            userMapper.updateById(user.setFrequency(user.getFrequency()+50));
            videoMapper.updateById(video.setStatus("failed"));
        }
    }

    @Override
    public List<Video> listPublic(Integer page) {
        return videoMapper.selectPage(new Page<>(page, 12), new QueryWrapper<Video>().lambda()
                .orderByDesc(Video::getCreatedTime)
                .eq(Video::getStatus, "succeeded")
                .eq(Video::getIsPublic, true)).getRecords();
    }

    @Override
    public VideoDetail detail(Long videoId) {
        return videoDetailMapper.selectOne(new QueryWrapper<VideoDetail>().lambda()
                .eq(VideoDetail::getVideoId, videoId));
    }

    @Override
    public void toggle(Long videoId) {
        videoMapper.updateById(new Video().setVideoId(videoId).setIsPublic(!videoMapper.selectById(videoId).getIsPublic()));
    }

    @Override
    public void delete(Long videoId) {
        aliUploadUtils.deleteFile(videoMapper.selectById(videoId).getVideoUrl());
        videoMapper.deleteById(videoId);
        videoDetailMapper.delete(new QueryWrapper<VideoDetail>().lambda()
                .eq(VideoDetail::getVideoId, videoId));
    }
}
