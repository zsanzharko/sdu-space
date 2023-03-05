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
import kz.sdu.space.component.service.storage.ImageStorageService;
import kz.sdu.space.component.service.storage.MarkDownStorageService;
import kz.sdu.space.exception.IdNotFoundException;
import kz.sdu.space.exception.InvalidInputException;
import kz.sdu.space.exception.storage.StorageException;
import kz.sdu.space.exception.storage.StorageItemNotFoundException;
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
public class MinioImageStorageServiceImpl implements ImageStorageService, MarkDownStorageService {
  private static final String BASE_MARKDOWN_PATH = "markdownFiles";
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
      throw new RuntimeException("Failed to upload image file to file storage", e);
    } catch (IOException e) {
      throw new InvalidInputException(e.getMessage());
    }
  }

  @Override
  public void updateImage(MultipartFile multipartFile, String path) {
    //TODO realize method
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
  public void deleteAllImages(String componentPath, Long id) {
    final String path = String.format("%s/%d/%s", componentPath, id, BASE_IMAGE_PATH);
    if (fileIsExist(path)) {
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
    if (fileIsExist(absolutePath)) {
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
  public String getImageBasePath() {
    return BASE_IMAGE_PATH;
  }

  @Override
  public boolean invalidateImagePath(String path) {
    //TODO match end dot with format
    String regex = String.format("^.*/\\d+/%s/.*$", getImageBasePath());
    return !path.matches(regex);
  }

  private static void insertBytesToStream(InputStream stream, ByteArrayOutputStream output) throws IOException {
    byte[] buffer = new byte[1024];
    int n;
    while ((n = stream.read(buffer)) != -1) {
      output.write(buffer, 0, n);
    }
  }

  @Override
  public void uploadMarkdown(InputStream inputStream, String objectName, String contentType) {
    try (inputStream) {
      PutObjectArgs args = PutObjectArgs.builder()
              .bucket(bucket)
              .object(objectName)
              .contentType(contentType)
              .stream(inputStream, -1, maxObjectSize)
              .build();
      minio.putObject(args);
    } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException("Failed to upload markdown file to MinIO", e);
    } catch (IOException e) {
      throw new InvalidInputException(e.getMessage());
    }
  }

  @Override
  public void updateMarkdown(MultipartFile multipartFile, String path) {
    //TODO realize method
  }

  @Override
  public byte[] getMarkdown(String objectName) {
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
          throw new IdNotFoundException("Markdown content not found");
        }
      }
    }
    throw new RuntimeException("Have problem with getting markdown file");
  }

  @Override
  public void deleteMarkdown(String absolutePath) {
    if (fileIsExist(absolutePath)) {
      try {
        minio.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(absolutePath)
                .build());
      } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
        throw new StorageException(e.getMessage());
      }
    }
  }

  @Override
  public String getMarkdownBasePath() {
    return BASE_MARKDOWN_PATH;
  }

  @Override
  public String getMarkdownAbsolutePath(String basePath, Long id, String fileName) {
    String absoluteFilePath = String.format("%s/%s/%s/%s",
            basePath, id, getMarkdownBasePath(), fileName);
    if (invalidateMarkdownPath(absoluteFilePath)) {
      throw new RuntimeException("Error with generating name for image");
    }
    return absoluteFilePath;
  }

  @Override
  public String getMarkdownAbsolutePath(String basePath, Long id) {
    String absoluteFilePath = String.format("%s/%s/%s",
            basePath, id, getMarkdownBasePath());
    if (invalidateMarkdownPath(absoluteFilePath)) {
      throw new RuntimeException("Error with generating name for image");
    }
    return absoluteFilePath;
  }

  @Override
  public String getImageAbsolutePath(String basePath, Long id, String fileName) {
    String absoluteFilePath = String.format("%s/%s/%s/%s",
            basePath, id, getImageBasePath(), fileName);
    if (invalidateImagePath(absoluteFilePath)) {
      throw new RuntimeException("Error with generating name for image");
    }
    return absoluteFilePath;
  }

  @Override
  public String getImageAbsolutePath(String basePath, Long id) {
    String absoluteFilePath = String.format("%s/%s/%s",
            basePath, id, getImageBasePath());
    if (invalidateImagePath(absoluteFilePath)) {
      throw new RuntimeException("Error with generating name for image");
    }
    return absoluteFilePath;
  }

  @Override
  public boolean invalidateMarkdownPath(String path) {
    //TODO match end dot with format
    String regexFile = String.format("^.*/\\d+/%s/.*$", getMarkdownBasePath());
    String regexFolder = String.format("^.*/\\d+/%s$", getMarkdownBasePath());
    return !(path.matches(regexFile) || path.matches(regexFolder));
  }

  @Override
  public boolean fileIsExist(String path) {
    try {
      minio.statObject(StatObjectArgs.builder()
              .bucket(bucket)
              .object(path).build());
      return true;
    } catch (ErrorResponseException e) {
      throw new StorageItemNotFoundException(e.errorResponse().message());
    } catch (Exception e) {
      throw new StorageException(e.getMessage());
    }
  }
}
