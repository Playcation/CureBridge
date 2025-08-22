package com.example.commonmodule.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

  @Value("${cloud.aws.credentials.access-key}")
  private String accessKey;
  @Value("${cloud.aws.credentials.secret-key}")
  private String secretKey;
  @Value("${cloud.aws.region.static}")
  private String region;

  @Value("${cloud.aws.credentials.board-access-key}")
  private String boardAccessKey;
  @Value("${cloud.aws.credentials.board-secret-key}")
  private String boardSecretKey;


  @Bean
  public AmazonS3Client amazonS3Client() {
    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
    return (AmazonS3Client) AmazonS3ClientBuilder.standard()
        .withRegion(region)
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        .build();
  }

  @Bean(name = "boardS3Client")
  public AmazonS3Client boardS3Client() {
    BasicAWSCredentials awsCreds = new BasicAWSCredentials(boardAccessKey, boardSecretKey);
    return (AmazonS3Client) AmazonS3ClientBuilder.standard()
        .withRegion("eu-north-1")
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//        .withPathStyleAccessEnabled(true) // 점 포함 버킷 이름 허용
        .build();
  }
}