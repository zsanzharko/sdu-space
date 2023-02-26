package kz.sdu.space.component.event;

import kz.sdu.space.component.Converter;
import kz.sdu.space.component.CrudServiceOperations;
import kz.sdu.space.component.event.dto.EventDto;
import kz.sdu.space.exception.InvalidInputException;

import java.io.IOException;
import java.util.List;

public interface EventComponent extends CrudServiceOperations<EventDto>,
        Converter<EventDto, Event> {
  //TODO need to think about image process
  void updateImages(Long eventId, List<EventImageTransfer> imagesTransfer) throws InvalidInputException, IOException;

  void uploadImage(EventImageTransfer dto, Event entity) throws IOException;
  byte[] getImage(String objectName) throws InvalidInputException;
}
