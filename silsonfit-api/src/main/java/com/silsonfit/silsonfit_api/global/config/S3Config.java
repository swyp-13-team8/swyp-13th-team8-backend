package com.silsonfit.silsonfit_api.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS S3 클라이언트 설정 클래스
 */
@Configuration
public class S3Config {

    @Value("${aws.s3.credentials.access-key}")
    private String accessKey;

    @Value("${aws.s3.credentials.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    /**
     * S3Client 빈 등록
     * AWS SDK v2 사용
     *
     * @return
     */
    @Bean
    public S3Client s3Client() {
        // AccessKey, SecretKey 기반 인증 객체 생성
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // S3Client 생성 및 반환
        return S3Client.builder()
                .region(Region.of(region)) // 리전 설정
                .credentialsProvider(StaticCredentialsProvider.create(credentials)) // 인증 정보 설정
                .build();
    }
}
