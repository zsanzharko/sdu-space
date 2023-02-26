package kz.sdu.space.component.minio;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import kz.sdu.space.component.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class MinioImageStorageServiceImpl implements ImageStorageService {
  private final MinioClient minio;
  private final String bucket;
  private final long maxObjectSize;

  public MinioImageStorageServiceImpl(MinioClient minio,
                                      @Value("${spring.minio.bucket}") String bucket,
                                      @Value("${spring.minio.max_object_size}") long maxObjectSize) {
    this.minio = minio;
    this.bucket = bucket;
    this.maxObjectSize = maxObjectSize;
  }

  @PostConstruct
  private void checkInitBucket() throws MinioException, IOException,
          NoSuchAlgorithmException, InvalidKeyException {
    boolean found = minio.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    if (!found) {
      minio.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
    } else {
      log.info(String.format("Bucket '%s' already connected.", bucket));
    }
  }

  @Override
  public void uploadImage(InputStream inputStream, String objectName, String contentType) {
    try (inputStream) {
      PutObjectArgs args = PutObjectArgs.builder()
              .bucket(bucket)
              .object(objectName)
              .contentType(contentType)
              .stream(inputStream, -1, maxObjectSize)
              .build();
      minio.putObject(args);
    } catch (Exception e) {
      throw new RuntimeException("Failed to upload file to MinIO", e);
    }
  }

  @Override
  public byte[] getImage(String objectName) {
    try {
      GetObjectArgs args = GetObjectArgs.builder()
              .bucket(bucket)
              .object(objectName)
              .build();
      InputStream stream = minio.getObject(args);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      insertBytesToStream(stream, output);
      return output.toByteArray();
    } catch (IOException | MinioException |
             InvalidKeyException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteAll() {
  }

  @Override
  public boolean deleteImage(String objectName) {
    try {
      minio.removeObject(RemoveObjectArgs.builder()
              .bucket(bucket)
              .object(objectName)
              .build());
      return true;
    } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
      System.out.println("Error occurred: " + e);
    }
    return false;
  }

  private static void insertBytesToStream(InputStream stream, ByteArrayOutputStream output) throws IOException {
    byte[] buffer = new byte[1024];
    int n;
    while ((n = stream.read(buffer)) != -1) {
      output.write(buffer, 0, n);
    }
  }
}
