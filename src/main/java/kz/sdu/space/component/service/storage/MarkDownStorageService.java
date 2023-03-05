package kz.sdu.space.component.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MarkDownStorageService {
  void uploadMarkdown(InputStream inputStream, String objectName, String contentType);

  void updateMarkdown(MultipartFile multipartFile, String path);

  byte[] getMarkdown(String objectName);

  void deleteMarkdown(String absolutePath);

  String getMarkdownBasePath();

  String getMarkdownAbsolutePath(String basePath, Long id, String fileName);
  String getMarkdownAbsolutePath(String basePath, Long id);

  boolean invalidateMarkdownPath(String path);
}
