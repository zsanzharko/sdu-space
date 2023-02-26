package kz.sdu.space.component.event;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class EventImageTransfer {
  private String name;
  private String contentType;
  private MultipartFile multipartFile;
}
