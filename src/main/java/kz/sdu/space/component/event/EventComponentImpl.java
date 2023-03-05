package kz.sdu.space.component.event;

import kz.sdu.space.component.event.dto.EventDto;
import kz.sdu.space.component.event.dto.EventForm;
import kz.sdu.space.component.minio.MinioImageStorageServiceImpl;
import kz.sdu.space.component.service.storage.ImageStorageService;
import kz.sdu.space.component.service.storage.MarkDownStorageService;
import kz.sdu.space.exception.IdNotFoundException;
import kz.sdu.space.exception.InvalidInputException;
import kz.sdu.space.exception.storage.StorageItemNotFoundException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EventComponentImpl implements EventComponent {
  private static final String BASE_EVENT_PATH = "events";
  private final EventRepository eventRepository;
  private final EventRuleConfiguration ruleConfiguration;
  private final ImageStorageService imageStorageService;
  private final MarkDownStorageService markDownStorageService;

  public EventComponentImpl(EventRuleConfiguration ruleConfiguration, EventRepository eventRepository,
                            MinioImageStorageServiceImpl storageService) {
    this.eventRepository = eventRepository;
    this.ruleConfiguration = ruleConfiguration;
    this.imageStorageService = storageService;
    this.markDownStorageService = storageService;
  }

  @Override
  public EventDto create(@NonNull EventForm eventForm) {
    validEventTitle(eventForm.getTitle());
    validDateTime(eventForm.getDateEvent());

    EventDto eventDto = eventForm.getDataTransfer();
    return convertEntity(eventRepository.save(convertDto(eventDto)));
  }

  @Override
  @Transactional
  public EventDto create(EventForm eventForm, MultipartFile markdownFile) {
    validEventTitle(eventForm.getTitle());
    validDateTime(eventForm.getDateEvent());
    validateMarkDownFile(markdownFile);

    EventDto eventDto = eventForm.getDataTransfer();
    Event event = eventRepository.save(convertDto(eventDto));

    try {
      final String markdownFileName = generateFileName(markdownFile.getOriginalFilename());
      markDownStorageService.uploadMarkdown(
              markdownFile.getInputStream(),
              markdownFileName,
              markdownFile.getContentType()
      );
      event.setContentUUID(markdownFileName);
    } catch (IOException e) {
      throw new InvalidInputException("Can't upload markdown file.");
    }
    return convertEntity(event);
  }

  private void validateMarkDownFile(MultipartFile markdownFile) {
    if (markdownFile == null || markdownFile.isEmpty()) {
      throw new InvalidInputException("Markdown file can't read or  does not exist");
    }
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
  public void updateImages(Long eventId, List<MultipartFile> multipartFileList) throws InvalidInputException, IOException {
    validImageSize(multipartFileList.size());
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new InvalidInputException("Id don't found"));

    for (MultipartFile file : multipartFileList) {
      final String fileName = generateFileName(file.getOriginalFilename());
      String path = imageStorageService.getImageAbsolutePath(BASE_EVENT_PATH, eventId, fileName);
      if (getImage(eventId, path) == null) {
        imageStorageService.uploadImage(file.getInputStream(), path,
                file.getContentType());
        event.addImageId(fileName);
        eventRepository.save(event);
      }
    }
    List<String> comingImageIds = multipartFileList.stream()
            .map(MultipartFile::getName)
            .toList();
    // process removing don't use images
    for (String imageId : event.getImageIdList()) {
      if (comingImageIds.contains(imageId)) {
        final String path = imageStorageService.getImageAbsolutePath(BASE_EVENT_PATH, eventId, imageId);
        imageStorageService.deleteImage(path);
      }
    }
  }

  private void validImageSize(Integer size) {
    if (size >= ruleConfiguration.getMaxSizeImage()) {
      //TODO need another exception http status code
      throw new InvalidInputException(String.format("Photos more than %d",
              ruleConfiguration.getMaxSizeImage()));
    }
  }

  @Override
  public void delete(@NonNull Long id) {
    if (eventRepository.existsById(id)) {
      final String imageAbsolutePath = imageStorageService.getImageAbsolutePath(BASE_EVENT_PATH, id);
      final String markDownAbsolutePath = markDownStorageService.getMarkdownAbsolutePath(BASE_EVENT_PATH, id);
      eventRepository.deleteById(id);
      imageStorageService.deleteAllImages(imageAbsolutePath, id);
      markDownStorageService.deleteMarkdown(markDownAbsolutePath);
    }
  }

  @Override
  @Transactional
  public void uploadImage(MultipartFile file, Long eventId) {
    Optional<Event> optionalEvent = eventRepository.findById(eventId);
    if (optionalEvent.isEmpty()) {
      throw new IdNotFoundException();
    }
    optionalEvent.ifPresent(event -> {
      validImageSize(event.getImageIdList().size());
      try {
        final String fileName = generateFileName(file.getOriginalFilename());
        final String path = imageStorageService.getImageAbsolutePath(BASE_EVENT_PATH, eventId, fileName);
        event.addImageId(fileName);
        eventRepository.save(event);

        imageStorageService.uploadImage(file.getInputStream(), path, file.getContentType());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public byte[] getImage(Long eventId, String originalFilename) throws InvalidInputException {
    final String path = imageStorageService.getImageAbsolutePath(BASE_EVENT_PATH, eventId, originalFilename);
    return imageStorageService.getImage(path);
  }

  @Override
  @Transactional
  public void deleteImage(Long eventId, String fileName) {
    final String path = String.format("%s/%d/%s/%s",
            BASE_EVENT_PATH, eventId, imageStorageService.getImageBasePath(), fileName);
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IdNotFoundException("Event don't found"));
    if (!event.getImageIdList().contains(fileName)) {
      throw new StorageItemNotFoundException();
    }else {
      event.removeImageId(fileName);
    }
    eventRepository.save(event);
    imageStorageService.deleteImage(path);
  }

  private void validId(EventDto eventDto) throws InvalidInputException {
    if (eventDto.getId() == null || eventRepository.findById(eventDto.getId()).isEmpty()) {
      throw new InvalidInputException("Incorrect date. Enter the id");
    }
  }

  private String generateFileName(String originalFilename) {
    //TODO realize format 'filename.png' from content-type
    if (originalFilename == null) {
      throw new InvalidInputException("File must have name with format.");
    }
    return UUID.randomUUID() + originalFilename.trim().substring(originalFilename.indexOf('.'));
  }

  @Override
  public EventDto convertEntity(@NonNull Event event) {
    return EventDto.builder()
            .id(event.getId())
            .imageIdList(event.getImageIdList())
            .title(event.getTitle())
            .description(event.getDescription())
            .dateEvent(event.getDateEvent())
            .contentUUID(event.getContentUUID())
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
            .contentUUID(eventDto.getContentUUID())
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
