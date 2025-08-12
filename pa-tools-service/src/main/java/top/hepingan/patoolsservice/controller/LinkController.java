package top.hepingan.patoolsservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.hepingan.pacommon.dto.LinkDto;
import top.hepingan.pacommon.msg.Result;
import top.hepingan.patoolsservice.service.LinkService;

@RequestMapping("/link")
@RestController
@Slf4j
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @GetMapping(value = "/getTopImg", name = "获取链接顶图")
    public Result getTopImg() {
        return Result.data(linkService.getTopImg());
    }

    @GetMapping(value = "/getList", name = "获取全部链接",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getLinkList(@RequestParam("linkName") String linkName){
        return Result.data(linkService.getAllLinkList(linkName));
    }

    @GetMapping(value = "/userList", name = "查询用户链接",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result selectUserLinkList(){
        return Result.data(linkService.getUserLinkList());
    }

    @PostMapping(value = "/applyLink", name = "申请链接", consumes = "multipart/form-data")
    public Result applyLink(@Valid LinkDto linkDto){
        linkService.applyLink(linkDto);
        return Result.ok();
    }

    @PostMapping(value = "/allowLink/{id}", name = "同意链接")
    public Result allowLink(@PathVariable Long id){
        try {
            linkService.allowLink(id);
            return Result.ok();
        }catch (Exception e){
            return Result.error(e.getMessage());
        }

    }

    @PostMapping(value = "/refuseLink/{id}", name = "撤销链接")
    public Result refuseLink(@PathVariable Long id){
        try {
            log.info("撤销link{}",id);
            linkService.refuseLink(id);
            return Result.ok();
        }catch (Exception e){
            return Result.error(e.getMessage());
        }

    }

    @PostMapping(value = "/deleteLink/{id}",name = "删除链接")
    public Result deleteLink(@PathVariable Long id){
        try {
            log.info("删除链接{}",id);
            linkService.deleteLink(id);
            return Result.ok();
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @PostMapping(value = "/setLinkHot/{id}",name = "设置链接为热门")
    public Result setLinkHot(@PathVariable Long id){
        try {
            log.info("上热门链接{}",id);
            linkService.setLinkHot(id);
            return Result.ok();
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @PostMapping(value = "/deleteLinkHot/{id}",name = "取消链接为热门")
    public Result deleteLinkHot(@PathVariable Long id){
        try {
            log.info("取消热门链接{}",id);
            linkService.deleteLinkHot(id);
            return Result.ok();
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @PostMapping(value = "/addStatLink/{id}",name = "用户添加收藏")
    public Result addStatLink(@PathVariable Long id){
        try {
            linkService.addStatLink(id);
            return Result.ok();
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @GetMapping(value = "/selectStatLink",name = "查询用户收藏")
    public Result selectStatLink(){
        return Result.data(linkService.selectStatLink());
    }


    @PostMapping(value = "/cancelStatLink/{id}",name = "用户取消收藏")
    public Result cancelStatLink(@PathVariable Long id){
        log.info("用户取消收藏链接{}",id);
        linkService.cancelStatLink(id);
        return Result.ok();
    }

    @GetMapping(value = "/selectStatLink/{id}",name = "查询单个收藏")
    public Result selectStatLinkById(@PathVariable Long id){
        return Result.data(linkService.selectStatLinkById(id));
    }

    @PostMapping(value = "/link/add", name = "通过链接")
    public Result allowLinkAdmin(@RequestParam final Long LinkId) {
        linkService.allowLink(LinkId);
        return Result.ok();
    }

    @PostMapping(value = "/link/delete", name = "删除链接", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result deleteLinkAdmin(@RequestBody @Validated final Long id) {
        linkService.deleteLink(id);
        return Result.ok();
    }


    @GetMapping(value = "/link/getPersonalList", name = "获取个人链接", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getLink() {
        return Result.data(linkService.getPersonalLinkList());
    }



    @PostMapping(value = "/link/setTopImg", name = "修改链接顶图")
    public Result setTopImg(@RequestBody @Validated final String imgUrl) {
        linkService.setTopImg(imgUrl);
        return Result.ok();
    }
}

