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

import javax.annotation.PostConstruct;
import java.io.File;
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
            minioClient.uploadObject(UploadObjectArgs.builder()
                    .filename(filename)
                    .bucket(bucketName)
                    .build());
            return bucketName + "/" + filename;
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


//    public InputStream getFileInputStream(String filePath) throws ReedBaseException, RuntimeException {
//        String[] split = filePath.split("/");
//        if (split.length < 2) {
//            throw new ReedMinioException();
//        }
//        String bucket = split[0];
//        StringBuilder fileNameBuilder = new StringBuilder();
//        for (int i = 1; i < split.length; i++) {
//            fileNameBuilder.append(split[i]);
//        }
//        String filename = fileNameBuilder.toString();
//        boolean bucketExist = bucketExist(bucket);
//        if (!bucketExist) {
//            throw new ReedMinioException();
//        }
//        try {
//            GetObjectResponse object = minioClient.statObject(GetObjectArgs.builder().bucket(bucket).object(filename).build());
//
//            return null;
//        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
//                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
//                 XmlParserException e) {
//            throw new ReedMinioException();
//        }
//    }

}
