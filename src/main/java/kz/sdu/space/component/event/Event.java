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

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "event", schema = "event_schema")
@Data
@Builder
public class Event {
  public Event() {
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
  @Temporal(TemporalType.DATE)
  private Date dateEvent;
}