package top.hepingan.pasystemservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.hepingan.pacommon.dto.*;
import top.hepingan.pacommon.exceptions.RegistrationException;
import top.hepingan.pasystemservice.feignClient.CodeFeignService;
import top.hepingan.pacommon.msg.Result;
import top.hepingan.pasystemservice.feignClient.LinkFeignService;
import top.hepingan.pasystemservice.service.AuthService;
import top.hepingan.pasystemservice.service.PayService;
import top.hepingan.pasystemservice.service.ServerService;
import top.hepingan.pasystemservice.service.UserService;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ServerService serverService;

    private final UserService userService;

    private final PayService payService;

    private final CodeFeignService codeFeignService;

    private final LinkFeignService linkFeignService;

    private final AuthService authService;


    /**
     * 生成兑换码
     *
     * @param dto the dto
     * @return the bot configuration
     */
    @PostMapping(value = "/exchange/build", name = "生成对话码", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result buildExchangeCode(@RequestBody @Validated final PutExchangeDto dto) {
        serverService.buildRedemptionCode(dto);
        return Result.ok();
    }


    /**
     * 更新服务器配置参数
     *
     * @param dto the dto
     * @return the bot configuration
     */
    @PostMapping(value = "/server/put/config", name = "用于保存或更新服务器参数", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result heavyLoadServerConfig(@RequestBody @Validated final ServerConfigDto dto) {
        serverService.heavyLoadDisposition(dto);
        return Result.ok();
    }

    /**
     * 获取服务器配置
     *
     * @return the bot configuration
     */
    @GetMapping(value = "/server/get/config", name = "获取服务器配置", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getServerConfig() {
        return Result.data(serverService.getDisposition());
    }


    /**
     * 更新终端
     *
     * @param dto the dto
     * @return the bot configuration
     */
    @PostMapping(value = "/server/put/terminal", name = "用于保存或更新终端参数", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result putTerminalConfig(@RequestBody @Validated final TerminalConfigDto dto) {
        serverService.putTerminal(dto);
        return Result.ok();
    }


    /**
     * 分页获取用户信息
     *
     * @param pageNum the page num
     * @param prompt  the prompt
     * @return the bot configuration
     */
    @GetMapping(value = "/user/get/page", name = "分页获取用户信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getUserPages(@RequestParam(defaultValue = "1") final int pageNum, final String prompt) {

        return Result.data(
                userService.getUserPageVo(pageNum, prompt)
        );
    }

    /**
     * 获取平台总人数
     */
    @GetMapping(value = "/user/get/count", name = "获取平台总人数", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getPlatformCount() {
        return Result.data(
                userService.getTotalUsers()
        );
    }

    /**
     * 分页获取订单信息
     *
     * @param pageNum the page num
     * @param status  the prompt
     * @return the bot configuration
     */
    @GetMapping(value = "/orders/page", name = "分页获取订单信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getOrdersPages(@RequestParam(defaultValue = "1") final int pageNum, @RequestParam final String prompt, @RequestParam final String status) {
        return Result.data(
                payService.getUserOrderPage(pageNum, prompt, status)
        );
    }

    /**
     * 新增产品
     *
     * @return the bot configuration
     */
    @PostMapping(value = "/product/put/data", name = "新增产品", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result productsShelf(@RequestBody @Validated ShelvesProductDto dto) {
        payService.shelvesProduct(dto);
        return Result.ok();
    }

    /**
     * 分页获取用户信息
     *
     * @param pageNum the page num
     * @param prompt  the prompt
     * @return the bot configuration
     */
    @GetMapping(value = "/product/get/page", name = "分页获取产品", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getProductPages(@RequestParam(defaultValue = "1") final int pageNum, final String prompt) {
        return Result.data(
                payService.getProductPageVo(pageNum, prompt)
        );
    }

    /**
     * 删除产品
     *
     * @return the bot configuration
     */
    @PostMapping(value = "/product/delete/{id}", name = "删除产品", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result productsDelete(@PathVariable final long id) {
        payService.deleteProductById(id);
        return Result.ok();
    }


    /**
     * 修改用户信息
     *
     * @param dto the dto
     * @return the bot configuration
     */
    @PostMapping(value = "/user/put/data", name = "修改用户数据信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateUserById(@RequestBody @Validated final UserPutDto dto) {
        userService.updateById(dto);
        return Result.ok();
    }

    /**
     * 分页获取兑换码
     *
     * @param pageNum the page num
     * @param prompt  the prompt
     * @return the result
     */
    @GetMapping(value = "/exchange/get/page", name = "兑换码列表", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result redemptionCodePage(@RequestParam(defaultValue = "1") int pageNum, @RequestParam final String prompt) {

        return Result.data(serverService.getRedemptionCodePage(pageNum, prompt));
    }

    /**
     * 根据ID删除兑换码
     *
     * @param id the id
     * @return the result
     */
    @PostMapping(value = "/exchange/delete/{id}", name = "删除兑换码", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result deleteExchangeById(@PathVariable final Long id) {
        serverService.deleteRedemptionCodeBasedOnTheId(id);
        return Result.ok();
    }

    /**
     * 更新公告
     *
     * @return the result
     */
    @PostMapping(value = "/put/announcement", name = "更新公告", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result putAnnouncement(@RequestBody @Validated final AnnouncementDto dto) {
        serverService.putAnnouncement(dto);
        return Result.ok();
    }

    @GetMapping(value = "/code/list", name = "获取源码列表")
    public Result getCodeList() {
        return codeFeignService.getCodeList();
    }

    @PostMapping(value = "/code/pass/{id}", name = "源码通过审核")
    public Result passCode(@PathVariable Long id) {
        return codeFeignService.passCode(id);
    }

    @PostMapping(value = "/code/reject/{id}", name = "源码撤销审核")
    public Result rejectCode(@PathVariable Long id) {
        return codeFeignService.rejectCode(id);
    }

    @PostMapping(value = "/link/add", name = "通过链接")
    public Result allowLink(@RequestParam final Long LinkId) {
        linkFeignService.addLink(LinkId);
        return Result.ok();
    }

    @PostMapping(value = "/link/delete", name = "删除链接", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result deleteLink(@RequestBody @Validated final Long id) {
        linkFeignService.deleteLink(id);
        return Result.ok();
    }


    @GetMapping(value = "/link/getPersonalList", name = "获取个人链接", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getLink() {
        return Result.data(linkFeignService.getPersonalLinkList());
    }



    @PostMapping(value = "/link/setTopImg", name = "修改链接顶图")
    public Result setTopImg(@RequestBody @Validated final String imgUrl) {
        linkFeignService.setTopImg(imgUrl);
        return Result.ok();
    }

    @PostMapping(value = "/submitEmail", name = "发送邮箱")
    public Result submitEmail(@RequestBody @Validated final EmailContentDto emailContentDto) {
        try {
            authService.submitEmailContent(emailContentDto);
            return Result.ok();
        } catch (RegistrationException e) {
            return Result.error(e.getMessage());
        }
    }

}
