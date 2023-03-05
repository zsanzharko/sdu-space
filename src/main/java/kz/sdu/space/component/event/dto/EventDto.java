package kz.sdu.space.component.event.dto;

import kz.sdu.space.component.DataTransfer;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class EventDto implements DataTransfer {
  private Long id;
  private List<String> imageIdList;
  private String title;
  private String description;
  private Timestamp dateEvent;
  private String contentUUID;
}