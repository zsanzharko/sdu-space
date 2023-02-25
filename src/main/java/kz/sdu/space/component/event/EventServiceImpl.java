package kz.sdu.space.component.event;

import kz.sdu.space.exception.IdNotFoundException;
import kz.sdu.space.exception.InvalidInputException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EventServiceImpl implements EventService {
  private final EventRepository eventRepository;
  private final EventRuleConfiguration ruleConfiguration;

  public EventServiceImpl(EventRuleConfiguration ruleConfiguration, EventRepository eventRepository) {
    this.eventRepository = eventRepository;
    this.ruleConfiguration = ruleConfiguration;
  }

  @Override
  public EventDto create(@NonNull Event event) throws InvalidInputException {
    validEventTitle(event.getTitle());
    validDateTime(event.getDateEvent());

    return convertEntity(eventRepository.save(event));
  }

  @Override
  public EventDto read(@NonNull Long id) throws IdNotFoundException {
    Optional<Event> event = eventRepository.findById(id);
    if (event.isPresent()) {
      return convertEntity(event.get());
    }
    throw new IdNotFoundException(String.format("Event with id: %d not found", id));
  }

  @Override
  public void update(@NonNull EventDto eventDto) throws InvalidInputException {
    validId(eventDto);
    Event event = convertDto(eventDto);

    validEventTitle(eventDto.getTitle());
    validDateTime(eventDto.getDateEvent());
    validImages(eventDto.getId(), eventDto.getImageIdList());

    eventRepository.save(event);
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

  private void validImages(Long eventId, List<UUID> imageIdList) throws InvalidInputException {
    if (imageIdList.size() > ruleConfiguration.getMaxSizeImage()) {
      throw new InvalidInputException(String.format("Photos more than %d",
              ruleConfiguration.getMaxSizeImage()));
    }
    //TODO write a code about image search and adding to another method
  }

  private void validDateTime(Date dateEvent) throws InvalidInputException {
    if (dateEvent == null || dateEvent.before(Date.valueOf(LocalDate.now()))) {
      throw new InvalidInputException("Incorrect date. Please enter correct date");
    }
  }

  private void validEventTitle(String title) throws InvalidInputException {
    if (title == null || title.isEmpty() || title.length() >= 255) {
      throw new InvalidInputException("Title is empty. Please enter the number");
    }
  }
}
