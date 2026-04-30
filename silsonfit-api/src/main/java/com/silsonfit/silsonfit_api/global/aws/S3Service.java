package com.silsonfit.silsonfit_api.global.aws;

import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    /**
     * PDF 파일을 받아 S3 버킷에 업로드 후 URL 반환
     *
     * @param fileBytes PDF 파일의 byte 배열
     * @param originalFileName PDF 파일의 원본파일명
     * @param contentType PDF 파일의 contentType
     * @return S3 URL
     */
    public String upload(byte[] fileBytes, String originalFileName, String contentType) {
        // 파일명 중복 방지를 위한 UUID 추가
        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;

        try {
            // S3에 올릴 요청 객체 생성 - PutObjectRequest
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(uniqueFileName)
                    .contentDisposition(contentType) // "application/pdf"
                    .build();

            // 실제 파일 업로드 실행
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(fileBytes));

            log.info("S3 파일 업로드 성공 - fileName={}", uniqueFileName);

            // 업로드된 파일의 S3 URL 생성 및 반환
            return getS3FileUrl(uniqueFileName);
        } catch (Exception e) {
            log.error("S3 업로드 중 오류 발생", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * S3 URL 기반으로 해당 파일을 버킷에서 삭제한다.
     *
     * @param pdfFileUrl S3 전체 URL
     */
    public void delete(String pdfFileUrl) {
        // URL 에서 파일명만 추출 (S3의 키가 됨)
        String key = extractKeyFromUrl(pdfFileUrl);

        // 삭제 요청 객체 생성 - DeleteObjectRequest
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // S3 파일 삭제
        s3Client.deleteObject(deleteObjectRequest);

        log.info("S3 파일 삭제 성공 - key={}", key);
    }

    private String extractKeyFromUrl(String pdfFileUrl) {
        int lastIndexOf = pdfFileUrl.lastIndexOf("/");
        return pdfFileUrl.substring(lastIndexOf + 1);
    }

    private String getS3FileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, region, fileName);
    }
}
