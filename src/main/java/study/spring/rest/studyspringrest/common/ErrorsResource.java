package study.spring.rest.studyspringrest.common;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;
import study.spring.rest.studyspringrest.index.IndexController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
	public ErrorsResource(Errors errors, Link... links) {
		super(errors, links);
		add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
	}
}
