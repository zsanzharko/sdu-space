package kz.sdu.space.component.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MarkDownStorageService extends StorageService {
  void uploadMarkdown(InputStream inputStream, String objectName, String contentType);

  void updateMarkdown(MultipartFile multipartFile, String path);

  byte[] getMarkdown(String objectName);

  void deleteMarkdown(String absolutePath);

  String getMarkdownBasePath();

  String getMarkdownAbsolutePath(String basePath, Long id, String fileName);

  boolean invalidateMarkdownPath(String path);

  String getMarkdownAbsolutePath(String baseEventPath, Long id);
}
