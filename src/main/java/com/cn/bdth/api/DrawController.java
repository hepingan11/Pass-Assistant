package com.cn.bdth.api;

import com.cn.bdth.dto.MessageDto;
import com.cn.bdth.dto.ZhipuDrawDto;
import com.cn.bdth.msg.Result;
import com.cn.bdth.service.DrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/draw")
@RequiredArgsConstructor
public class DrawController {

    private final DrawService drawService;


    @PostMapping(value = "/zhipu/image",name = "cogview绘画接口")
    public Result addZhipuDrawingTask(@RequestParam("prompt") String prompt,
                                      @RequestParam("size") String size,
                                      @RequestParam("quality") String quality,
                                      @RequestParam("model") String model,
                                      @RequestPart(value = "file", required = false) MultipartFile file) {
        ZhipuDrawDto dto = new ZhipuDrawDto()
                .setPrompt(prompt)
                .setSize(size)
                .setQuality(quality)
                .setModel(model);
        return Result.data(drawService.addZhipuDrawingTask(dto,file));
    }

    @GetMapping(value = "/list/public",name = "公共图片列表")
    public Result listPublic(@RequestParam Integer page) {
        return Result.data(drawService.listPublic(page));
    }

    @GetMapping(value = "/list/private",name = "私有图片列表")
    public Result listPrivate(@RequestParam Integer page) {
        return Result.data(drawService.listPrivate(page));
    }

    @GetMapping(value = "/toggle",name = "切换公共状态")
    public Result toggle(@RequestParam Long drawId) {
        drawService.toggle(drawId);
        return Result.ok();
    }



}
