package kz.sdu.space.component.event;

import kz.sdu.space.component.Converter;
import kz.sdu.space.component.CrudServiceOperations;
import kz.sdu.space.component.event.dto.EventDto;
import kz.sdu.space.component.event.dto.EventForm;
import kz.sdu.space.exception.InvalidInputException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventComponent extends CrudServiceOperations<EventDto, EventForm>,
        Converter<EventDto, Event> {
  //TODO need to think about image process
  void updateImages(Long eventId, List<MultipartFile> multipartFileList) throws InvalidInputException, IOException;

  void uploadImage(MultipartFile file, Long eventId);

  byte[] getImage(Long eventId, String originalFilename) throws InvalidInputException;
}
