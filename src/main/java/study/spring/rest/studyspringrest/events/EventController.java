package study.spring.rest.studyspringrest.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import study.spring.rest.studyspringrest.common.ErrorsResource;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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

	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id) {
		Optional<Event> optionalEvent = eventRepository.findById(id);
		if (optionalEvent.isEmpty())
			return ResponseEntity.notFound().build();
		EventResource eventResource = new EventResource(optionalEvent.get());
		eventResource.add(new Link("/docs/index.html#resource-get-event").withRel("profile"));
		return ResponseEntity.ok(eventResource);
	}
	@GetMapping
	public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
		Page<Event> page = this.eventRepository.findAll(pageable);
		var entityModels = assembler.toModel(page, e -> new EventResource(e));
		entityModels.add(new Link("/docs/index.html#resource-events-query").withRel("profile"));
		return ResponseEntity.ok(entityModels);
	}

	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
		if (errors.hasErrors()) {
			return getBadRequest(errors);
		}
		eventValidator.validate(eventDto, errors);

		if (errors.hasErrors()) {
			return getBadRequest(errors);
		}
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
		eventResource.add(new Link("docs/index.html#resources-events-create").withRel("profile"));
		return ResponseEntity.created(createdUri).body(eventResource);
	}

	private ResponseEntity getBadRequest(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}
}
