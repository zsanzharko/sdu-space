package kz.sdu.space.service;

import java.io.InputStream;

public interface ImageStorageService {

  void uploadImage(InputStream inputStream, String objectName, String contentType);
  byte[] getImage(String objectName);
  void deleteAll();
  boolean deleteImage(String objectName);
}
