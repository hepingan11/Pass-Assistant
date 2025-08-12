package top.hepingan.paaiservice.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import top.hepingan.paaiservice.service.DrawService;
import top.hepingan.pacommon.dto.ZhipuDrawDto;
import top.hepingan.pacommon.utils.AliUploadUtils;




@Service
@RequiredArgsConstructor
@Slf4j
public class DrawServiceImpl implements DrawService {

    private final AliUploadUtils aliUploadUtils;


    @Override
    public String addZhipuDrawingTask(ZhipuDrawDto dto) {
        return "";
    }
}
