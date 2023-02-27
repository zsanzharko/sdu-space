package kz.sdu.space.component.event.controller;

import kz.sdu.space.component.event.EventComponent;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(
        value = "events/images"
)
public class EventImageRestController {
  private final EventComponent eventComponent;

  public EventImageRestController(EventComponent eventComponent) {
    this.eventComponent = eventComponent;
  }

  @GetMapping(
          value = "/{event}/{uuid}",
          produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
  public @ResponseBody byte[] getImageByUUID(
          @PathVariable(name = "event") Long eventId,
          @PathVariable(name = "uuid") String uuid) {
    return eventComponent.getImage(eventId, uuid);
  }

  @PostMapping("/{eventId}")
  public ResponseEntity<?> uploadImage(@PathVariable Long eventId,
                                       @RequestParam("file") MultipartFile file) {
    eventComponent.uploadImage(file, eventId);
    return ResponseEntity.ok().build();
  }
}
