package top.hepingan.pacommon.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.hepingan.pacommon.exceptions.UploadException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/**
 * 雨纷纷旧故里草木深
 *
 * @author  @github dulaiduwang003
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AliUploadUtils {

    @Value("${ali-oss.endpoint}")
    private String endpoint;

    @Value("${ali-oss.accessKey}")
    private String accessKey;

    @Value("${ali-oss.secretKey}")
    private String secretKey;

    @Value("${ali-oss.bucketName}")
    private String bucketName;

    public String uploadFile(final MultipartFile file, final String path, final String newFileName, final boolean isImage) {
        OSS ossClient = new OSSClientBuilder()
                .build(endpoint, accessKey, secretKey);
        try (InputStream inputStream = file.getInputStream()) {
            String originalFileName = file.getOriginalFilename();

            assert originalFileName != null;
            String fileName;
            fileName = Objects.requireNonNullElseGet(newFileName, () -> UUID.randomUUID() + originalFileName.substring(originalFileName.lastIndexOf('.')));

            String filePath = path + "/" + fileName;

            if (isImage) {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType("image/jpg");
                ossClient.putObject(bucketName, filePath, inputStream, objectMetadata);
            } else {
                ossClient.putObject(bucketName, filePath, inputStream);
            }

            return "/" + filePath;
        } catch (IOException e) {
            log.error("无法将图片上传到阿里云。错误消息： {} 错误类： {}", e.getMessage(), e.getClass());
            throw new UploadException();
        } finally {
            ossClient.shutdown();
        }
    }



    public void deleteFile(final String filePath) {
        OSS ossClient = new OSSClientBuilder()
                .build(endpoint, accessKey, secretKey);
        try {

            if (filePath.startsWith("/")) {
                ossClient.deleteObject(bucketName, filePath.substring(1));
            } else {
                ossClient.deleteObject(bucketName, filePath);
            }
        } catch (OSSException | ClientException e) {
            log.error("无法从阿里云删除图片。错误消息： {} 错误类： {}", e.getMessage(), e.getClass());
        } finally {
            ossClient.shutdown();
        }


    }




}
