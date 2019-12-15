package study.spring.rest.studyspringrest.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

// BeanSerializer
//public class EventResource extends RepresentationModel {
//
//	@JsonUnwrapped // Event Json Object로 감싸지 않는다.
//	private Event event;
//
//	public EventResource(Event event) {
//		this.event = event;
//	}
//
//	public Event getEvent() {
//		return event;
//	}
//}
public class EventResource extends EntityModel<Event> {
	public EventResource(Event event, Link... links) {
		super(event, links);
		// 맨 밑에 코드와 같지만 type-safe 하지 않다.
		// add(new Link("http://localhost:8080/api/events/" + event.getId()));
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}
}

