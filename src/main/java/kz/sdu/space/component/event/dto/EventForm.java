package kz.sdu.space.component.event.dto;

import kz.sdu.space.component.RequestForm;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class EventForm implements RequestForm<EventDto> {
  private String title;
  private String description;
  private Timestamp dateEvent;

  @Override
  public EventDto getDataTransfer() {
    return EventDto.builder()
            .title(title)
            .description(description)
            .dateEvent(dateEvent)
            .build();
  }
}
