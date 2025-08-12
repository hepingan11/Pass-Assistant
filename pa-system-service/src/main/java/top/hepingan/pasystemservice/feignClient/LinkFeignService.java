package top.hepingan.pasystemservice.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import top.hepingan.pacommon.msg.Result;

@FeignClient(name = "pa-tools-service",contextId = "linkFeignService")
public interface LinkFeignService {

    @PostMapping(value = "/link/add", name = "通过链接")
    Result addLink(@RequestParam Long LinkId);

    @PostMapping(value = "/link/delete", name = "删除链接", produces = MediaType.APPLICATION_JSON_VALUE)
    Result deleteLink(@RequestBody @Validated Long id);

    @GetMapping(value = "/link/getPersonalList", name = "获取个人链接", produces = MediaType.APPLICATION_JSON_VALUE)
    Result getPersonalLinkList();

    @PostMapping(value = "/link/setTopImg", name = "修改链接顶图")
    Result setTopImg(@RequestBody @Validated String imgUrl);
}
