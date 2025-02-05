package com.jihun.mysimpleblog.auth.service;

import com.jihun.mysimpleblog.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static com.jihun.mysimpleblog.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final ResourceLoader resourceLoader;

    @Value("${file.upload.path}")
    private String uploadPath;

    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(FILE_IS_EMPTY);
        }

        String filename = UUID.randomUUID().toString() + getExtension(file.getOriginalFilename());

        try {
            // 절대 경로 얻기
            Path absolutePath = Paths.get(uploadPath).toAbsolutePath();
            log.info("Upload Path: {}", absolutePath);

            // 디렉토리 생성
            if (!Files.exists(absolutePath)) {
                Files.createDirectories(absolutePath);
            }

            // 파일 저장
            Path filePath = absolutePath.resolve(filename);
            log.info("File Path: {}", filePath);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/images/" + filename;
        } catch (IOException e) {
            log.error("File upload error", e);
            throw new CustomException(FILE_UPLOAD_ERROR);
        }
    }

    public void deleteFile(String imageUrl) {
        try {
            // URL에서 파일명 추출
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadPath, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new CustomException(FILE_DELETE_ERROR);
        }
    }


    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    // 이미지 파일 형식 검증 (선택적)
    private void validateImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(INVALID_FILE_TYPE);
        }
    }
}