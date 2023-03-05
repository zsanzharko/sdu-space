package kz.sdu.space.component.event.controller;

import kz.sdu.space.component.event.EventComponent;
import kz.sdu.space.restcontrolleradvice.responseentity.BasicResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("markdown/event/")
@RequiredArgsConstructor
public class EventMarkdownRestController {
  private final EventComponent eventComponent;

  @PostMapping("{eventId}")
  public BasicResponseEntity<?> uploadMarkdownForEvent(@PathVariable Long eventId,
                                                       @RequestParam("file") MultipartFile markdownFile) {
    eventComponent.uploadMarkdownFile(eventId, markdownFile);
    return new BasicResponseEntity<>("Markdown file is upload");
  }

  @GetMapping("{eventId}/{markdownUUID}")
  public byte[] getMarkdownByEvents(@PathVariable Long eventId, @PathVariable String markdownUUID) {
    return eventComponent.getMarkdown(eventId, markdownUUID);
  }

  @DeleteMapping("{eventId}/{markdownUUID}")
  public BasicResponseEntity<?> uploadMarkdownForEvent(@PathVariable Long eventId,@PathVariable String markdownUUID) {
    eventComponent.deleteMarkdown(eventId, markdownUUID);
    return new BasicResponseEntity<>("Markdown file is deleted");
  }
}
