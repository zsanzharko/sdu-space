package kz.sdu.space.component.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ImageStorageService {
  void uploadImage(InputStream inputStream, String objectName, String contentType);

  void updateImage(MultipartFile multipartFile, String path);

  byte[] getImage(String objectName);

  void deleteAll(String path, Long id);

  void deleteImage(String absolutePath);

  String getBasePath();
}
