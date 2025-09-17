package com.cn.bdth.service;

import com.cn.bdth.dto.ZhipuDrawDto;
import com.cn.bdth.entity.Drawing;
import com.cn.bdth.vo.admin.DrawDetailVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DrawService {


    String addZhipuDrawingTask(ZhipuDrawDto dto, MultipartFile file);

    List<DrawDetailVo> listPublic(Integer page);

    List<Drawing> listPrivate(Integer page);

    void toggle(Long drawId);
}
