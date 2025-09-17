package com.cn.bdth.service;



import com.cn.bdth.dto.DoubaoVideoCallback;
import com.cn.bdth.dto.VideoDto;
import com.cn.bdth.entity.Video;
import com.cn.bdth.entity.VideoDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {

    String createTask(String prompt, MultipartFile file);

    List<Video> listPrivate(Integer page);

    void callback(DoubaoVideoCallback doubaoVideoCallback);

    List<Video> listPublic(Integer page);

    VideoDetail detail(Long videoId);

    void toggle(Long videoId);

    void delete(Long videoId);
}
