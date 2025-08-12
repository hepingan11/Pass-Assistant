package top.hepingan.patoolsservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import top.hepingan.pacommon.entity.Work;
import top.hepingan.pacommon.mapper.WorkMapper;
import top.hepingan.pacommon.msg.Result;
import top.hepingan.patoolsservice.service.FunnyService;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/funny")
@RestController
@Slf4j
@RequiredArgsConstructor
public class FunnyController {

    private final FunnyService funnyService;

    private final WorkMapper workMapper;

    @GetMapping(value = "/work/get", name = "获取作品列表")
    public Result getWorkList() {
        List<Work> list = workMapper.selectList(new QueryWrapper<Work>()
                .lambda()
                .select(Work::getUrl, Work::getName, Work::getWorkId,Work::getSort,Work::getCreatedTime,Work::getUpdatedTime)
        );
        return Result.data(list);
    }

    @PostMapping(value = "/work/add", name = "添加作品",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result addWork(@RequestParam final String url,@RequestParam final String name,@RequestParam final String sort) {
        try {
            funnyService.addWork(url, name, sort);
            return Result.ok();
        }catch (Exception e){
            return Result.error(String.valueOf(e));
        }

    }

    @PostMapping(value = "/work/delete/{id}", name = "删除作品",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result deleteWork(@PathVariable final Long id) {
        workMapper.deleteById(id);
        return Result.ok();
    }

    @PostMapping(value = "/work/update", name = "修改作品",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateWork(@RequestBody final Work work) {
        try{
            work.setUpdatedTime(LocalDateTime.now());
            workMapper.updateById(work);
            return Result.ok();
        }catch (Exception e){
            return Result.error(String.valueOf(e));
        }
    }
}
