package top.hepingan.patoolsservice.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.hepingan.pacommon.entity.Work;
import top.hepingan.pacommon.mapper.WorkMapper;
import top.hepingan.patoolsservice.service.FunnyService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FunnyServiceImpl implements FunnyService {

    private final WorkMapper workMapper;


    @Override
    public void addWork(String url, String name, String sort) {
        Work work = new Work().setUrl(url).setName(name).setSort(sort)
                        .setCreatedTime(LocalDateTime.now()).setUpdatedTime(LocalDateTime.now());
        workMapper.insert(work);
    }

}
