package com.cn.bdth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.bdth.entity.Photo;
import com.cn.bdth.enums.FileEnum;
import com.cn.bdth.mapper.PhotoMapper;
import com.cn.bdth.service.PhotoService;
import com.cn.bdth.utils.AliUploadUtils;
import com.cn.bdth.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoMapper photoMapper;

    private final AliUploadUtils aliUploadUtils;

    @Override
    public String upload(List<MultipartFile> file) {
        Long photoNum = photoMapper.selectCount(new QueryWrapper<Photo>().eq("user_id", UserUtils.getCurrentLoginId()));
        if (photoNum == 20L){
            return "为防止被刷最多存储20张";
        }
        for (MultipartFile item : file) {
            final Long currentLoginId = UserUtils.getCurrentLoginId();
            final String fileName = UUID.randomUUID() + ".jpg";
            String url=aliUploadUtils.uploadFile(item, FileEnum.LINK.getDec(),fileName,true);
            LocalDateTime time = LocalDateTime.now();
            Photo photo = new Photo().setName(fileName)
                    .setUrl(url)
                    .setUserId(currentLoginId)
                    .setIsPublic(0)
                    .setCreatedTime(time);
            photoMapper.insert(photo);
        }
        return "上传成功";

    }

    @Override
    public List<Photo> getPhoto() {
        return photoMapper.selectList(new QueryWrapper<Photo>().eq("user_id", UserUtils.getCurrentLoginId()).orderBy(true,false,"created_time"));
    }

    @Override
    public void deletePhoto(Long id) {
        String filePath = photoMapper.selectById(id).getUrl();
        aliUploadUtils.deleteFile(filePath);
        photoMapper.deleteById(id);
    }

    @Override
    public void setPhotoPublic(Long id) {
        Photo photoId = photoMapper.selectOne(new QueryWrapper<Photo>().eq("photo_id", id));
        if (photoId.getIsPublic() == 1){
            photoMapper.update(new Photo().setIsPublic(0), new QueryWrapper<Photo>().lambda().eq(Photo::getPhotoId, id));
        }else {
            photoMapper.update(new Photo().setIsPublic(1), new QueryWrapper<Photo>().lambda().eq(Photo::getPhotoId, id));
        }
    }

    @Override
    public List<Photo> getPublicPhoto() {
        return photoMapper.selectList(new QueryWrapper<Photo>().eq("is_public", 1).orderBy(true,false,"created_time"));
    }


}
