package com.secureauthenticationapp.authenticationapp.unitTest.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.secureauthenticationapp.authenticationapp.domain.exception.CustomFileUploadException;
import com.secureauthenticationapp.authenticationapp.domain.exception.FileDeleteException;
import com.secureauthenticationapp.authenticationapp.domain.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private StorageService storageService;

    private MultipartFile file;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());
        ReflectionTestUtils.setField(storageService, "bucketName", "bucketname");
        ReflectionTestUtils.setField(storageService, "region", "region");
    }

    @Test
    void uploadFile_success() throws IOException {
        String actualFileUrl = storageService.uploadFile(file);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
        assertTrue(actualFileUrl.matches("https://bucketname\\.s3\\.region\\.amazonaws\\.com/\\d+-test\\.jpg"));
    }

    @Test
    void uploadFile_invalidFileType_throwsException() {
        MultipartFile invalidFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        assertThrows(CustomFileUploadException.class, () -> storageService.uploadFile(invalidFile));
    }

    @Test
    void uploadFile_exceedsSizeLimit_throwsException() {
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6 MB
        MultipartFile largeFile = new MockMultipartFile("file", "large.jpg", "image/jpeg", largeContent);
        assertThrows(CustomFileUploadException.class, () -> storageService.uploadFile(largeFile));
    }

    @Test
    void deleteFile_success() {
        String fileUrl = "https://bucketname.s3.region.amazonaws.com/test.jpg";
        storageService.deleteFile(fileUrl);

        ArgumentCaptor<DeleteObjectRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(argumentCaptor.capture());
        DeleteObjectRequest capturedRequest = argumentCaptor.getValue();

        assertEquals("bucketname", capturedRequest.getBucketName());
        assertEquals("test.jpg", capturedRequest.getKey());
    }

    @Test
    void deleteFile_failure_throwsException() {
        ArgumentCaptor<DeleteObjectRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);

        doThrow(new AmazonServiceException("Error deleting file"))
                .when(s3Client)
                .deleteObject(argumentCaptor.capture());

        String fileUrl = "https://bucketname.s3.region.amazonaws.com/test.jpg";

        assertThrows(FileDeleteException.class, () -> storageService.deleteFile(fileUrl));

        DeleteObjectRequest capturedRequest = argumentCaptor.getValue();
        assertEquals("bucketname", capturedRequest.getBucketName());
        assertEquals("test.jpg", capturedRequest.getKey());
    }
}

