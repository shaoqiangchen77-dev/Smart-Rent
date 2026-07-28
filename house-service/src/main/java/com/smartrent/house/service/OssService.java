package com.smartrent.house.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class OssService {

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.access-key-id}")
    private String accessKeyId;

    @Value("${oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${oss.bucket-name}")
    private String bucketName;

    @Value("${oss.url-prefix}")
    private String urlPrefix;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    /**
     * 上传文件到 OSS
     *
     * @param file 文件
     * @param dir  目录前缀，如 "house/images"
     * @return 文件访问URL
     */
    public String upload(MultipartFile file, String dir) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectName = dir + "/" + dateStr + "/" + UUID.randomUUID() + extension;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);
            // 上传即设为公共读，避免对象是私有导致浏览器匿名加载 403
            ossClient.setObjectAcl(bucketName, objectName, CannedAccessControlList.PublicRead);
        }

        return urlPrefix + "/" + objectName;
    }

    /**
     * 删除文件
     */
    public void delete(String url) {
        try {
            String objectName = url.replace(urlPrefix + "/", "");
            ossClient.deleteObject(bucketName, objectName);
        } catch (Exception e) {
            log.error("删除OSS文件失败: {}", url, e);
        }
    }
}
