package top.hepingan.patoolsservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.hepingan.pacommon.msg.Result;
import top.hepingan.patoolsservice.service.PhotoService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping(value = "/upload",name = "上传",produces= MediaType.APPLICATION_JSON_VALUE)
    public Result upload(@RequestParam("files") List<MultipartFile> file){
        try{
            String msg = photoService.upload(file);
            return Result.data(msg);
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @GetMapping(value = "/getUserList",name = "获取用户图片",produces= MediaType.APPLICATION_JSON_VALUE)
    public Result getPhoto(){
        try{
            return Result.data(photoService.getPhoto());
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @PostMapping(value = "/delete/{id}",name = "删除图片",produces= MediaType.APPLICATION_JSON_VALUE)
    public Result deletePhoto(@PathVariable Long id){
        try{
            photoService.deletePhoto(id);
            return Result.ok();
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @PostMapping(value = "/setPhotoPublic/{id}",name = "设置为公共")
    public Result setPhotoPublic(@PathVariable Long id){
        try{
            photoService.setPhotoPublic(id);
            return Result.data(1);
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @GetMapping(value = "/getAllImgList",name = "获取公共图片")
    public Result getAllPhoto(){
        try{
            return Result.data(photoService.getPublicPhoto());
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

}
