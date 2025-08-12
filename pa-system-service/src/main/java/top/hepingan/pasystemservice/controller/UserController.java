package top.hepingan.pasystemservice.controller;



import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.hepingan.pacommon.dto.StarDialogueDto;
import top.hepingan.pacommon.exceptions.UploadException;
import top.hepingan.pacommon.msg.Result;
import top.hepingan.pasystemservice.service.StarService;
import top.hepingan.pasystemservice.service.UserService;



@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final StarService starService;

//    private final GptService gptService;
//
//    /**
//     * 获取用户个性GPT配置
//     *
//     * @return the result
//     */
//    @GetMapping(value = "/personality/get/config", name = "获取用户个性GPT配置", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Result getPersonalityGptConfig() {
//        return Result.data(gptService.getPersonalityConfig(null));
//    }
//
//    /**
//     * 设置用户个性GPT配置
//     *
//     * @return the result
//     */
//    @PostMapping(value = "/personality/put/config", name = "设置用户个性GPT配置", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Result putPersonalityGptConfig(@RequestBody @Validated PersonalityDto dto) {
//        gptService.putPersonalityConfig(dto);
//        return Result.ok();
//    }

    /**
     * 当前用户信息结果。
     *
     * @return the result
     */
    @PostMapping(value = "/current/info", name = "用户当前用户信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result currentUserInfo() {

        return Result.data(userService.getCurrentUserInfo());

    }


    /**
     * 用户更新头像
     *
     * @param avatar the avatar
     * @return the result
     */
    @PostMapping(value = "/upload/avatar", name = "用户更新头像", consumes = "multipart/form-data")
    public Result userUploadAvatar(@Valid @NotNull(message = "用户头像不能为空") final MultipartFile avatar) {
        try {
            userService.editUserAvatar(avatar);
            return Result.ok();
        } catch (UploadException e) {
            return Result.error(e.getMessage());
        }

    }

    /**
     * 修改用户昵称
     *
     * @param userName the username
     * @return the result
     */
    @PostMapping(value = "/upload/username", name = "用户更新用户名")
    public Result userUpdateName(@NotBlank(message = "用户昵称不能为空") @RequestParam String userName) {
        userService.editUserName(userName);
        return Result.ok();
    }


    /**
     * 分页获取收藏
     *
     * @param pageNum the page num
     * @return the result
     */
    @GetMapping(value = "/stat/page", name = "分页获取收藏", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getStarPage(@RequestParam(defaultValue = "1") final int pageNum) {
        return Result.data(starService.getUserStarVoPage(pageNum));
    }


    /**
     * 删除指定收藏
     *
     * @param id the id
     * @return the result
     */
    @PostMapping(value = "/stat/delete/{id}", name = "删除指定收藏", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result deleteStarById(@PathVariable final Long id) {
        starService.deleteById(id);
        return Result.ok();
    }


    /**
     * 查看指定收藏
     *
     * @param starId the star id
     * @return the result
     */
    @GetMapping(value = "/stat/get/data", name = "查看指定收藏", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getStarDetailById(@RequestParam final Long starId) {

        return Result.data(starService.getUserStarDetail(starId));
    }


    /**
     * 添加收藏
     *
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/stat/put/data", name = "添加收藏", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result putStarDialogue(@RequestBody @Validated final StarDialogueDto dto) {
        return Result.data(starService.starDialogue(dto));
    }


    /**
     * 获取我的作品管理
     *
     * @param pageNum the page num
     * @return the result
     */
    @GetMapping(value = "/drawing/page", name = "获取我的作品", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getUserOpsPage(@RequestParam(defaultValue = "1") final int pageNum) {
        return Result.data(null);
    }

    /**
     * 获取我的收藏(兼容WEB接口)
     *
     * @return the result
     */
    @GetMapping(value = "/stat/get/web", name = "获取我的收藏", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getUserStarWeb() {
        return Result.data(starService.getUserStarWeb());
    }

    @PostMapping(value = "/sign", name = "签到", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result sign() {
        return Result.data(userService.sign());
    }




}
