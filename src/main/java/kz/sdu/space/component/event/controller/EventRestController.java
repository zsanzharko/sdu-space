package kz.sdu.space.component.event.controller;

import kz.sdu.space.component.event.EventComponent;
import kz.sdu.space.component.event.dto.EventDto;
import kz.sdu.space.component.event.dto.EventForm;
import kz.sdu.space.restcontrolleradvice.responseentity.BasicResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "events")
@RequiredArgsConstructor
public class EventRestController {
  private final EventComponent eventComponent;

  @GetMapping
  public BasicResponseEntity<List<EventDto>> showAllEvents() {
    return new BasicResponseEntity<>(eventComponent.readAll());
  }

  @GetMapping("/event/{id}")
  public BasicResponseEntity<EventDto> getById(@PathVariable Long id) {
    return new BasicResponseEntity<>(eventComponent.read(id));
  }

  @PostMapping(value = "/event", consumes = "application/json")
  public BasicResponseEntity<EventDto> create(@RequestBody EventForm eventForm) {
    final EventDto eventDto = eventComponent.create(eventForm);
    return new BasicResponseEntity<>(eventDto);
  }

  @PutMapping("/event")
  public BasicResponseEntity<EventDto> update(@RequestBody EventDto eventDto) {
    eventComponent.update(eventDto);
    return new BasicResponseEntity<>("Saved.", eventDto);
  }

  @DeleteMapping("/event/{id}")
  public BasicResponseEntity<EventDto> delete(@PathVariable Long id) {
    eventComponent.delete(id);
    return new BasicResponseEntity<>("Deleted.");
  }
}
