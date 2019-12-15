package study.spring.rest.studyspringrest.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

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
		Event newEvent = this.eventRepository.save(event);
		URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		event.setId(10);
		return ResponseEntity.created(createdUri).body(event);
	}
}
