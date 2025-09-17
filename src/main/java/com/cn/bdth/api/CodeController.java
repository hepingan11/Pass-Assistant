package com.cn.bdth.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.bdth.dto.CodeDto;
import com.cn.bdth.entity.CodeExchange;
import com.cn.bdth.entity.CodeImage;
import com.cn.bdth.mapper.CodeExchangeMapper;
import com.cn.bdth.mapper.CodeImageMapper;
import com.cn.bdth.msg.Result;
import com.cn.bdth.service.CodeService;
import com.cn.bdth.utils.UserUtils;
import com.cn.bdth.vo.CodeListVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 源码市场api
 */
@RequestMapping("/code")
@RestController
@Slf4j
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    private final CodeImageMapper codeImageMapper;

    private final CodeExchangeMapper codeExchangeMapper;

    @GetMapping(value = "/codeList",name = "获取项目列表")
    public Result getCodeList(@RequestParam String name,@RequestParam String language) {
        return Result.data(codeService.codelist(name,language));
    }

    @PostMapping(value = "/code/add",name = "添加项目",consumes = "multipart/form-data")
    public Result addCode(@Valid CodeDto code) {
        try {
            codeService.addCode(code);
            return Result.ok();
        }catch (Exception e){
            return Result.error();
        }
    }

    @GetMapping(value = "/addLook",name = "添加点击次")
    public Result addLook(@RequestParam Long codeId) {
        codeService.addLook(codeId);
        return Result.ok();
    }

    @PostMapping(value = "/order/buy",name = "下单购买成功后")
    public Result orderBuy(@RequestParam Long codeId) {
        codeService.orderBuy(codeId);
        return Result.ok();
    }

    @GetMapping(value = "/buyList",name = "获取已购买的")
    public Result buyList() {
        List<CodeExchange> codeExchanges = codeExchangeMapper.selectList(new QueryWrapper<CodeExchange>().lambda().eq(CodeExchange::getBuyUserId, UserUtils.getCurrentLoginId())
                .eq(CodeExchange::getStatus, "已支付"));
        List<CodeListVo> codelist = codeService.codelist(null, null);
        // 过滤 codeList，只保留 codeId 在 codeExchanges 中的项
        List<CodeListVo> filteredCodeList = codelist.stream()
                .filter(code -> codeExchanges.stream().anyMatch(exchange -> exchange.getCodeId().equals(code.getCodeId())))
                .toList();
        return Result.data(filteredCodeList);
    }

    @PostMapping(value = "/buy/alipay",name = "支付宝购买项目")
    public Result buyAlipay(@RequestParam Long codeId) {
        return Result.data(codeService.generatePayQrCode(codeId));
    }

    @GetMapping(value = "/buy/alipay/status",name = "支付宝购买项目状态")
    public Result buyAlipayStatus(@RequestParam String orderNo) {
        return Result.data(codeService.paymentStatus(orderNo));
    }

    @GetMapping(value = "/myPublish",name = "获取我发布的项目")
    public Result myPublish() {
        List<CodeListVo> codelist = codeService.codelist(null, null);
        // 过滤 codeList，只保留 code.userId为 当前登录用户id
        List<CodeListVo> filteredCodeList = codelist.stream()
                .filter(code -> code.getUserId().equals(UserUtils.getCurrentLoginId()))
                .toList();
        return Result.data(filteredCodeList);
    }

    @PostMapping(value = "/myPublish/delete",name = "删除我发布的项目")
    public Result myPublishDelete(@RequestParam Long codeId) {
        List<CodeExchange> codeExchanges = codeExchangeMapper.selectList(new QueryWrapper<CodeExchange>().lambda().eq(CodeExchange::getCodeId, codeId));
        if (!codeExchanges.isEmpty()){
            return Result.error("该项目已被购买，无法删除");
        }
        codeImageMapper.delete(new QueryWrapper<CodeImage>().lambda().eq(CodeImage::getCodeId, codeId));
        codeService.removeById(codeId);
        return Result.ok();
    }

    @PostMapping(value = "/myPublish/update",name = "修改我发布的项目")
    public Result myPublishUpdate(@RequestBody CodeDto codeDto) {
        codeService.updateCode(codeDto);
        return Result.ok();
    }

    @PostMapping(value = "/myPublish/update/image",name = "修改我发布的项目图片")
    public Result myPublishUpdateImage(@RequestParam Long codeId,@RequestParam MultipartFile image) {
        codeService.updateCodeImage(codeId,image);
        return Result.ok();
    }

    @PostMapping(value = "/myPublish/update/image/delete",name = "修改我发布的项目图片删除")
    public Result myPublishUpdateImageDelete(@RequestParam String imageUrl) {
        codeService.deleteCodeImage(imageUrl);
        return Result.ok();
    }

    @GetMapping(value = "/mycode",name = "我的项目")
    public Result myCode() {
        return Result.data(codeService.myCode());
    }

}


