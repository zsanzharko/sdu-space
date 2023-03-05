package kz.sdu.space.component.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ImageStorageService extends StorageService {
  void uploadImage(InputStream inputStream, String objectName, String contentType);

  void updateImage(MultipartFile multipartFile, String path);

  byte[] getImage(String objectName);

  void deleteAllImages(String path, Long id);

  void deleteImage(String absolutePath);

  String getImageBasePath();

  String getImageAbsolutePath(String basePath, Long id, String fileName);

  boolean invalidateImagePath(String path);

  String getImageAbsolutePath(String baseEventPath, Long id);
}
