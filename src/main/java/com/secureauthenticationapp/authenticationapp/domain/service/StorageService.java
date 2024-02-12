package com.secureauthenticationapp.authenticationapp.domain.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.secureauthenticationapp.authenticationapp.domain.exception.CustomFileUploadException;
import com.secureauthenticationapp.authenticationapp.domain.exception.FileDeleteException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class StorageService {

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("image/jpeg", "image/png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private AmazonS3 s3Client;

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKeyId, this.secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

    public String uploadFile(MultipartFile file) {
        validateImageFile(file);
        String fileUrl = "";
        try {
            String fileName = generateFileName(file);
            fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), new ObjectMetadata())
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return fileUrl;
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new CustomFileUploadException("Error uploading file", e);
        }
    }

    public void deleteFile(String fileUrl) {
        String fileKey = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileKey));
            log.info("Deleted file from S3: {}", fileKey);
        } catch (AmazonServiceException e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw new FileDeleteException("Error deleting file from S3");
        }
    }

    private String generateFileName(MultipartFile multiPart) {
        try {
            return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
        } catch (Exception e) {
            log.error("Error generating file name: {}", e.getMessage());
            throw new CustomFileUploadException("Error generating file name");
        }
    }

    private void validateImageFile(MultipartFile file) throws CustomFileUploadException {
        if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
            throw new CustomFileUploadException("Invalid file type. Only JPEG and PNG are allowed.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomFileUploadException("File size exceeds the maximum allowed limit of 5 MB.");
        }
    }

}

