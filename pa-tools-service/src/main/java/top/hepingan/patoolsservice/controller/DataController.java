package top.hepingan.patoolsservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.hepingan.pacommon.msg.Result;
import top.hepingan.patoolsservice.service.DataService;

@RequestMapping("/data")
@RestController
@Slf4j
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    @GetMapping(value = "/getTimeFrequency/{type}", name = "获取时间频率")
    public Result getTimeFrequency(@PathVariable Integer type) {
        return Result.data(dataService.timeFrequency(type));
    }

    @GetMapping(value = "/getModelFrequency", name = "获取模型使用频率")
    public Result getModelFrequency() {
        return Result.data(dataService.getModelFrequency());
    }

    @GetMapping(value = "/getContentCloud", name = "获取问题词云")
    public Result getContentFrequency() {
        return Result.data(dataService.getWordFrequency(1));
    }

    @GetMapping(value = "/getMessageCloud", name = "获取回答词云")
    public Result getMessageFrequency() {
        return Result.data(dataService.getWordFrequency(2));
    }


}
