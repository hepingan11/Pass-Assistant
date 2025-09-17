package com.cn.bdth.service.impl;

import com.cn.bdth.entity.Work;
import com.cn.bdth.mapper.WorkMapper;
import com.cn.bdth.service.FunnyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
