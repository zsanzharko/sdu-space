package kz.sdu.space.component.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
  List<Event> findByDateEventBefore(Date fixedEndDate);
}
