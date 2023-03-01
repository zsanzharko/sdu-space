package kz.sdu.space.component.minio;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.annotation.PostConstruct;
import kz.sdu.space.component.service.ImageStorageService;
import kz.sdu.space.exception.IdNotFoundException;
import kz.sdu.space.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class MinioImageStorageServiceImpl implements ImageStorageService {
  private static final String BASE_IMAGE_PATH = "images";
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
    } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException("Failed to upload file to MinIO", e);
    } catch (IOException e) {
      throw new InvalidInputException(e.getMessage());
    }
  }

  @Override
  public void updateImage(MultipartFile multipartFile, String path) {

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
    } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | InsufficientDataException |
             InternalException | InvalidResponseException | ServerException | XmlParserException e) {
      throw new RuntimeException(e);
    } catch (ErrorResponseException e) {
      try (var response = e.response()) {
        if (response.code() == HttpStatus.NOT_FOUND.value()) {
          throw new IdNotFoundException("Image not found");
        }
      }
    }
    throw new RuntimeException("Have problem with getting image");
  }

  @Override
  public void deleteAll(String componentPath, Long id) {
    final String path = String.format("%s/%d/%s", componentPath, id, BASE_IMAGE_PATH);
    if (isObjectExist(path)) {
      try {
        minio.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build());
      } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException e) {
        throw new RuntimeException("Failed to delete file in MinIO", e);
      } catch (IOException e) {
        throw new InvalidInputException(e.getMessage());
      }
    }
  }

  @Override
  public void deleteImage(String absolutePath) {
    if (isObjectExist(absolutePath)) {
      try {
        minio.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(absolutePath)
                .build());
      } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public String getBasePath() {
    return BASE_IMAGE_PATH;
  }

  private boolean isObjectExist(final String path) {
    try {
      minio.statObject(StatObjectArgs.builder()
              .bucket(bucket)
              .object(path).build());
      return true;
    } catch (ErrorResponseException e) {
      e.printStackTrace();
      return false;
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private static void insertBytesToStream(InputStream stream, ByteArrayOutputStream output) throws IOException {
    byte[] buffer = new byte[1024];
    int n;
    while ((n = stream.read(buffer)) != -1) {
      output.write(buffer, 0, n);
    }
  }
}
