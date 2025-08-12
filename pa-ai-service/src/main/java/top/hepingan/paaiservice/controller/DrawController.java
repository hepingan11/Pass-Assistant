package top.hepingan.paaiservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.hepingan.paaiservice.service.DrawService;
import top.hepingan.pacommon.dto.ZhipuDrawDto;
import top.hepingan.pacommon.msg.Result;

@RestController
@Slf4j
@RequestMapping("/draw")
@RequiredArgsConstructor
public class DrawController {

    private final DrawService drawService;


    @PostMapping(value = "/zhipu/image",name = "cogview绘画接口")
    public Result addZhipuDrawingTask(@RequestBody ZhipuDrawDto dto) {
        return Result.data(drawService.addZhipuDrawingTask(dto));
    }
}
