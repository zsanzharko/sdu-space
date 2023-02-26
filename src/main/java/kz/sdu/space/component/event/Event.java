package kz.sdu.space.component.event;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "event", schema = "event_schema")
@Data
public class Event {
  public Event() {
  }

  @Builder
  public Event(Long id, List<UUID> imageIdList, String title, String description, Timestamp dateEvent) {
    this.id = id;
    this.imageIdList = imageIdList;
    this.title = title;
    this.description = description;
    this.dateEvent = dateEvent;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "image_uuid")
  @ElementCollection
  @CollectionTable(name = "event_images_id", schema = "event_schema",
          joinColumns = @JoinColumn(name = "id"))
  private List<UUID> imageIdList;
  @Column(name = "title")
  private String title;
  @Column(name = "description")
  private String description;

  @Column(name = "event_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Timestamp dateEvent;

  public void addImageId(UUID imageId) {
    if (imageIdList == null) {
      imageIdList = new ArrayList<>();
    }
    imageIdList.add(imageId);
  }

  public void removeImageId(UUID imageId) {
    if (imageIdList == null) {
      return;
    }
    imageIdList.remove(imageId);
  }
}