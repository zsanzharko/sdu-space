package kz.sdu.space.component.event;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class EventRuleConfiguration {
  @Value("${app.component.event.max_size_images}")
  private Integer maxSizeImage;
  @Value("${app.component.event.max_title_length}")
  private Integer maxTitleLength;
  @Value("${app.component.event.max_description_length}")
  private Integer maxDescriptionLength;
}
