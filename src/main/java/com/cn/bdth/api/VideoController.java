package com.cn.bdth.api;

import com.cn.bdth.dto.DoubaoVideoCallback;
import com.cn.bdth.msg.Result;
import com.cn.bdth.service.VideoService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping(value = "/createTask",name = "创建视频生成任务")
    public Result createTask(@RequestParam("prompt") String prompt,
                             @RequestPart(value = "file", required = false) MultipartFile file) {
        return Result.data(videoService.createTask(prompt,file));
    }

    @GetMapping(value = "/list/private",name = "获取视频列表")
    public Result list(@RequestParam Integer page) {
        return Result.data(videoService.listPrivate(page));
    }

    @PostMapping(value = "/callback",name = "豆包回调地址")
    public Result callback(@RequestBody DoubaoVideoCallback doubaoVideoCallback) {
        videoService.callback(doubaoVideoCallback);
        return Result.ok();
    }

    @GetMapping(value = "/list/public",name = "获取视频列表")
    public Result listPublic(@RequestParam Integer page) {
        return Result.data(videoService.listPublic(page));
    }

    @GetMapping(value = "/detail",name = "获取视频详情")
    public Result detail(@RequestParam Long videoId) {
        return Result.data(videoService.detail(videoId));
    }

    @GetMapping(value = "/toggle",name = "切换视频公开状态")
    public Result toggle(@RequestParam Long videoId) {
        videoService.toggle(videoId);
        return Result.ok();
    }

    @DeleteMapping(value = "/delete/{videoId}",name = "删除视频")
    public Result delete(@PathVariable Long videoId) {
        videoService.delete(videoId);
        return Result.ok();
    }
}
