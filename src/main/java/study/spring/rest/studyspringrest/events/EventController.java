package study.spring.rest.studyspringrest.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	EventValidator eventValidator;

	private final EventRepository eventRepository;

	// eventRepository가 빈으로 등록되어 있으므로 @Autowired 없이도
	// 의존성이 주입된다.
	public EventController(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
		if (errors.hasErrors())
			return ResponseEntity.badRequest().body(errors);
		eventValidator.validate(eventDto, errors);

		if (errors.hasErrors())
			return ResponseEntity.badRequest().body(errors);

		Event event = modelMapper.map(eventDto, Event.class);

		//Event Service가 있다면 위임하는 것이 더 좋다.
		event.update();
		Event newEvent = this.eventRepository.save(event);

		WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
		URI createdUri = selfLinkBuilder.toUri();
		EventResource eventResource = new EventResource(newEvent);
		eventResource.add(linkTo(EventController.class).withRel("query-events"));
		//self 링크는 항상 필요하기 때문에 EventResource에서 추가하는 것으로 변경 하였다.
		//self, update는 링크는 같기는 하지만 Relation만 다르다. (수정할 떄는 PUT 메소드로 들어온다.)
		eventResource.add(selfLinkBuilder.withRel("update-event"));
		return ResponseEntity.created(createdUri).body(eventResource);
	}
}
