package kz.sdu.space.component.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ImageStorageService {
  void uploadImage(InputStream inputStream, String objectName, String contentType);

  void updateImage(MultipartFile multipartFile, String path);

  byte[] getImage(String objectName);

  void deleteAllImages(String path, Long id);

  void deleteImage(String absolutePath);

  String getImageBasePath();

  String getImageAbsolutePath(String basePath, Long id, String fileName);
  String getImageAbsolutePath(String basePath, Long id);

  boolean invalidateImagePath(String path);
}
