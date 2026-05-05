package com.silsonfit.silsonfit_api.global.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    // Mock 객체 사용
    @Mock
    S3Client s3Client;

    // Mock S3Client 를 S3Service 에 주입
    @InjectMocks
    S3Service s3Service;

    @BeforeEach
    void setUp() {
        // @Value("${aws.s3.bucket}")에 들어갈 값을 테스트용으로 주입
        ReflectionTestUtils.setField(s3Service, "bucket", "test-bucket");

        // @Value("${aws.s3.region}")에 들어갈 값을 테스트용으로 주입
        ReflectionTestUtils.setField(s3Service, "region", "ap-northeast-2");
    }

    @Test
    void PDF_S3_업로드_테스트() {
        byte[] dummyFileBytes = "dummy pdf content".getBytes();
        String originalFileName = "test_약관.pdf";
        String contentType = "application/pdf";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String resultUrl = s3Service.upload(dummyFileBytes, originalFileName, contentType);

        System.out.println("생성된 S3 URL : " + resultUrl);

        // URL 이 https:~로 시작하는지
        assertThat(resultUrl).startsWith("https://test-bucket.s3.ap-northeast-2.amazonaws.com/");

        // URL 끝에 원본 파일명이 잘 붙어있는지
        assertThat(resultUrl).endsWith(originalFileName);

        // s3 업로드 중 s3Client 의 putObject 메서드가 실행됐는지 확인
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}