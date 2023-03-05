package kz.sdu.space.component.event.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class EventWithoutImageDto {
  private Long id;
  private String title;
  private String description;
  private Date dateEvent;
}
