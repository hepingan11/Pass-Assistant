package top.hepingan.patoolsservice.service;


import org.springframework.web.multipart.MultipartFile;
import top.hepingan.pacommon.entity.Photo;

import java.util.List;

public interface PhotoService {
    String upload(List<MultipartFile> file);

    List<Photo> getPhoto();

    void deletePhoto(Long id);

    void setPhotoPublic(Long id);

    List<Photo> getPublicPhoto();
}
