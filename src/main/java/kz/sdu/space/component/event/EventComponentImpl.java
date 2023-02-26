package kz.sdu.space.component.event;

import kz.sdu.space.component.event.dto.EventDto;
import kz.sdu.space.component.event.dto.EventForm;
import kz.sdu.space.exception.IdNotFoundException;
import kz.sdu.space.exception.InvalidInputException;
import kz.sdu.space.component.minio.MinioImageStorageServiceImpl;
import kz.sdu.space.component.service.ImageStorageService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EventComponentImpl implements EventComponent {
  private final EventRepository eventRepository;
  private final EventRuleConfiguration ruleConfiguration;
  private final ImageStorageService storageService;

  public EventComponentImpl(EventRuleConfiguration ruleConfiguration, EventRepository eventRepository,
                            MinioImageStorageServiceImpl storageService) {
    this.eventRepository = eventRepository;
    this.ruleConfiguration = ruleConfiguration;
    this.storageService = storageService;
  }

  @Override
  public EventDto create(@NonNull EventForm eventForm) {
    validEventTitle(eventForm.getTitle());
    validDateTime(eventForm.getDateEvent());

    EventDto eventDto = eventForm.getDataTransfer();
    return convertEntity(eventRepository.save(convertDto(eventDto)));
  }

  @Override
  public EventDto read(@NonNull Long id) {
    Optional<Event> event = eventRepository.findById(id);
    if (event.isPresent()) {
      return convertEntity(event.get());
    }
    throw new IdNotFoundException(String.format("Event with id: %d not found", id));
  }

  @Override
  public List<EventDto> readAll() {
    return eventRepository.findAll().stream()
            .map(this::convertEntity)
            .toList();
  }

  @Override
  public void update(@NonNull EventDto eventDto) throws InvalidInputException {
    validId(eventDto);
    Event event = convertDto(eventDto);

    validDateTime(eventDto.getDateEvent());
    eventRepository.save(event);
  }

  @Override
  public void updateImages(Long eventId, List<EventImageTransfer> imagesTransfer) throws InvalidInputException, IOException {
    if (imagesTransfer.size() > ruleConfiguration.getMaxSizeImage()) {
      throw new InvalidInputException(String.format("Photos more than %d",
              ruleConfiguration.getMaxSizeImage()));
    }
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new InvalidInputException("Id don't found"));

    for (EventImageTransfer imageTransfer : imagesTransfer) {
      String imageName = getImageName(imageTransfer.getName());
      String path = String.format("events/%d/images/%s", eventId, imageName);
      if (getImage(path) == null) {
        storageService.uploadImage(
                imageTransfer.getMultipartFile().getInputStream(),
                path,
                imageTransfer.getContentType()
        );
        event.addImageId(UUID.fromString(imageName));
      }
    }
    List<UUID> comingImageIds = imagesTransfer.stream()
            .map(i -> UUID.fromString(i.getName())).toList();

    // process removing don't use images
    for (UUID imageId : event.getImageIdList()) {
      if (!comingImageIds.contains(imageId)) {
        storageService.deleteImage(imageId.toString());
      }
    }
  }

  private String getImageName(String name) {
    UUID uuid = UUID.randomUUID();
    return String.format("%s-%s", uuid, name);
  }

  private void validId(EventDto eventDto) throws InvalidInputException {
    if (eventDto.getId() == null || eventRepository.findById(eventDto.getId()).isEmpty()) {
      throw new InvalidInputException("Incorrect date. Enter the id");
    }
  }

  @Override
  public void delete(@NonNull Long id) {
    eventRepository.deleteById(id);
  }

  @Override
  public void uploadImage(EventImageTransfer dto, Event entity) throws IOException {
    List<UUID> entityImageIds = entity.getImageIdList();
    byte[] image;
    boolean found = false;
    for (UUID imageId : entityImageIds) {
      //FIXME can't check contentType
      image = storageService.getImage(imageId.toString());
      if (Arrays.equals(dto.getMultipartFile().getBytes(), image)) {
        found = true;
        break;
      }
    }
    if (!found) {
      storageService.uploadImage(dto.getMultipartFile().getInputStream(),
              dto.getName(), dto.getContentType());
    }
  }

  @Override
  public byte[] getImage(String objectName) throws InvalidInputException {
    if (!validateImageName(objectName)) {
      throw new InvalidInputException("Uncorrected input file name");
    }

    return storageService.getImage(objectName);
  }

  private boolean validateImageName(String name) {
    String regex = "^events/\\d+/images/\\w+\\.\\w+$";
    return name.matches(regex);
  }

  @Override
  public EventDto convertEntity(@NonNull Event event) {
    return EventDto.builder()
            .id(event.getId())
            .imageIdList(event.getImageIdList())
            .title(event.getTitle())
            .description(event.getDescription())
            .dateEvent(event.getDateEvent())
            .build();
  }

  @Override
  public Event convertDto(@NonNull EventDto eventDto) {
    return Event.builder()
            .id(eventDto.getId())
            .imageIdList(eventDto.getImageIdList())
            .title(eventDto.getTitle())
            .description(eventDto.getDescription())
            .dateEvent(eventDto.getDateEvent())
            .build();
  }

  private void validDateTime(Timestamp dateEvent) throws InvalidInputException {
    if (dateEvent == null || dateEvent.before(Date.valueOf(LocalDate.now()))) {
      throw new InvalidInputException("Incorrect date. Please enter correct date");
    }
  }

  private void validEventTitle(String title) throws InvalidInputException {
    if (title == null || title.isEmpty() || title.length() >= ruleConfiguration.getMaxTitleLength()) {
      throw new InvalidInputException("Title is empty. Please enter the title");
    } else if (eventRepository.existsByTitle(title)) {
      throw new InvalidInputException("Title is exist . Please enter other title");
    }
  }
}
