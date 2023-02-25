package kz.sdu.space.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@Slf4j
public class MinioConfig {
  @Value("${spring.minio.url}")
  private String url;
  @Value("${spring.minio.bucket}")
  private String bucket;
  @Value("${spring.minio.access-key}")
  private String accessKey;
  @Value("${spring.minio.secret-key}")
  private String secretKey;

  @Bean
  public MinioClient getService() throws MinioException, IOException,
          NoSuchAlgorithmException, InvalidKeyException {
    MinioClient minioClient = MinioClient.builder()
            .endpoint(url, 9000, false)
            .credentials(accessKey, secretKey)
            .build();
    checkInitBucket(minioClient);
    return minioClient;
  }

  private void checkInitBucket(MinioClient minioClient) throws MinioException, IOException,
          NoSuchAlgorithmException, InvalidKeyException{
    boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    if (!found) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
    } else {
      log.info(String.format("Bucket '%s' already exists.", bucket));
    }
  }
}
