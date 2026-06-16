package com.certimaster.productivity.domain.service;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Stores uploaded PDF/ebook resources in object storage (MinIO). Files are kept
 * in a private bucket; downloads are served via short-lived presigned URLs so
 * only the owner (who must pass the API's auth + ownership check first) can read
 * them. The DB keeps only the object key, never a public link.
 */
@Service
public class ResourceFileStorageService {

    private static final Logger log = LoggerFactory.getLogger(ResourceFileStorageService.class);

    private static final String ALLOWED_CONTENT_TYPE = "application/pdf";
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25MB
    private static final int PRESIGNED_TTL_SECONDS = 300; // 5 minutes

    private final MinioClient minioClient;

    @Value("${minio.bucket.resource:resources}")
    private String bucketName;

    @Value("${minio.public-endpoint:http://localhost:9000}")
    private String publicEndpoint;

    @Value("${minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;

    /** Client bound to the browser-reachable endpoint, used only to sign GET URLs. */
    private MinioClient presignClient;

    public ResourceFileStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void init() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket: {}", bucketName, e);
        }
        this.presignClient = MinioClient.builder()
                .endpoint(publicEndpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /** Uploads a PDF and returns the storage object key. */
    public String uploadPdf(Long userId, MultipartFile file) {
        validate(file);
        String objectName = "user-" + userId + "/" + UUID.randomUUID() + ".pdf";
        try (InputStream in = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(in, file.getSize(), -1)
                    .contentType(ALLOWED_CONTENT_TYPE)
                    .build());
            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload PDF resource", e);
        }
    }

    /** A short-lived presigned GET URL the browser can open to read the file. */
    public String presignedGetUrl(String objectKey) {
        try {
            return presignClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectKey)
                    .expiry(PRESIGNED_TTL_SECONDS, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create download URL", e);
        }
    }

    public void delete(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to delete resource file: {}", objectKey, e);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum of 25MB");
        }
        if (!ALLOWED_CONTENT_TYPE.equals(file.getContentType())) {
            throw new IllegalArgumentException("File type not allowed. Only PDF is accepted");
        }
    }
}
