package mc.liu.polyrestructure.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Duration;

@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * 返回一个 presigned PUT URL，前端直接 PUT 整个对象（demo 用法，后续可改为 multipart）
     */
    public String presignPutObject(String objectKey, Duration expiry) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucket)
                        .object(objectKey)
                        .expiry((int) expiry.getSeconds())
                        .build()
        );
    }

    /**
     * 获取对象流（Worker 消费时使用）
     */
    public InputStream getObjectStream(String objectKey) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .build()
        );
    }

    /**
     * 流式上传一个对象（用于后续图片上传）
     */
    public void putObjectStream(String objectKey, InputStream in, long size, String contentType) throws Exception {
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .stream(in, size, -1)
                .contentType(contentType)
                .build();
        minioClient.putObject(args);
    }
}
