package kz.sdu.space.component.minio;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
  public MinioClient getService() {
    return MinioClient.builder()
            .endpoint(url, 9000, false)
            .credentials(accessKey, secretKey)
            .build();
  }
}
