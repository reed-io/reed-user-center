package org.reed.core.user.minio;

import io.minio.*;
import io.minio.errors.*;
import org.reed.core.user.define.ReedMinioException;
import org.reed.core.user.define.UserCenterException;
import org.reed.core.user.utils.CommonUtil;
import org.reed.exceptions.ReedBaseException;
import org.reed.log.ReedLogger;
import org.reed.utils.EnderUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.util.UriEncoder;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class AmazonS3StandardStorage {

    private MinioClient minioClient;

    @Value("${reed.minio.endpoint}")
    private String minioEndpoint;

    @Value("${reed.minio.access-key}")
    private String minioAccessKey;

    @Value("${reed.minio.secret-key}")
    private String minioSecretKey;

    public static final long MINIO_MINI_PART_SIZE = 5 *1024 *1024L;

    @PostConstruct
    private void init() {
        minioClient = MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(minioAccessKey, minioSecretKey)
                .build();;
    }

    private boolean bucketExist(String bucketName) throws ReedMinioException {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (ErrorResponseException | ServerException | XmlParserException | InsufficientDataException |
                 InternalException | InvalidKeyException | InvalidBucketNameException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException e) {
            ReedLogger.error(EnderUtil.devInfo() + "minio exception", e);
            throw new ReedMinioException();
        }
    }

    private void createBucket(String bucketName) throws ReedMinioException {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | RegionConflictException | InvalidBucketNameException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            ReedLogger.error(EnderUtil.devInfo() + "minio exception", e);
            throw new ReedMinioException();
        }

    }


    public String uploadFile(String bucketName, MultipartFile file, boolean isForce) throws ReedMinioException {
        boolean bucketExist = bucketExist(bucketName);
        if (!bucketExist) {
            if (isForce) {
                createBucket(bucketName);
            }else {
                ReedLogger.warn(EnderUtil.devInfo() + "bucket [" + bucketName + "] is not exist");
                throw new ReedMinioException("bucket [" + bucketName + "] is not exist");
            }
        }
        try {
            String filename = CommonUtil.getSnowFlakeId() + "_" + file.getOriginalFilename();
            long partSize = Math.max(file.getSize(), MINIO_MINI_PART_SIZE);
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), partSize)
                            .build());
            return UriEncoder.encode(bucketName) + "/" + UriEncoder.encode(filename);
        } catch (ErrorResponseException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException | InvalidBucketNameException |
                 InsufficientDataException e) {
            ReedLogger.error(EnderUtil.devInfo() + "minio exception", e);
            throw new ReedMinioException();
        }
    }

    public String uploadFile(String bucketName, MultipartFile file) throws ReedMinioException {
        return uploadFile(bucketName, file, false);
    }

}
