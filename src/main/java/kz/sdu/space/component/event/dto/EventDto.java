package kz.sdu.space.component.event.dto;

import kz.sdu.space.component.DataTransfer;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class EventDto implements DataTransfer {
  private Long id;
  private List<UUID> imageIdList;
  private String title;
  private String description;
  private Timestamp dateEvent;
}