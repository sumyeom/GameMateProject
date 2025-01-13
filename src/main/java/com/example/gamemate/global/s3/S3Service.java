package com.example.gamemate.global.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // ACL 설정 제거하고 기본 putObject 사용
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName,
                file.getInputStream(), metadata));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "-" + originalFileName;
    }

    public void deleteFile(String fileUrl) {
        try {
            // URL에서 파일 키(경로) 추출
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            // S3에서 파일 삭제
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 삭제 실패");
        }

    }
}
