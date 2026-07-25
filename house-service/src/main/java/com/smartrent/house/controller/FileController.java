package com.smartrent.house.controller;

import com.smartrent.common.result.R;
import com.smartrent.house.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final OssService ossService;

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public R<Map<String, String>> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "dir", defaultValue = "house/images") String dir) throws IOException {
        String url = ossService.upload(file, dir);
        return R.ok(Map.of("url", url));
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/delete")
    public R<Void> delete(@RequestParam(value = "url") String url) {
        ossService.delete(url);
        return R.ok();
    }
}
