package top.hepingan.pasystemservice.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import top.hepingan.pacommon.msg.Result;

@FeignClient(name = "pa-tools-service", contextId = "codeFeignService")
public interface CodeFeignService {

    @GetMapping("/code/list")
    Result getCodeList();

    @PostMapping("/code/pass/{id}")
    Result passCode(@PathVariable("id") Long id);

    @PostMapping("/code/reject/{id}")
    Result rejectCode(@PathVariable("id") Long id);

    @PostMapping("/code/exchange")
    Result exchangeCode();
}