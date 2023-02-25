package kz.sdu.space.component.event;

import kz.sdu.space.component.Converter;
import kz.sdu.space.component.CrudServiceOperations;

public interface EventService extends CrudServiceOperations<EventDto, Event>,
        Converter<EventDto, Event> {
}
