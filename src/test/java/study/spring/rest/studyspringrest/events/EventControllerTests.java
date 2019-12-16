package study.spring.rest.studyspringrest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import study.spring.rest.studyspringrest.common.RestDocsConfiguration;
import study.spring.rest.studyspringrest.common.TestDescription;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	EventRepository eventRepository;

	@Test
	// 주석보다 나은데?
	@TestDescription("정상적으로 이벤트를 생성하는 테스트")
	public void createEvent() throws Exception {
		EventDto eventDto = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 8, 2, 2))
				.closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 9, 2, 2))
				.beginEventDateTime(LocalDateTime.of(2019, 12, 13, 2, 2))
				.endEventDateTime(LocalDateTime.of(2019, 12, 14, 2, 2))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타텁 팩토리")
				.build();

		mockMvc.perform(post("/api/events/")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaTypes.HAL_JSON)
					.content(objectMapper.writeValueAsString(eventDto)))
				//print()를 통해 볼 수 있는 모든 내용을 andExpect로 확인 가능 합니다.
				.andDo(print())
				.andExpect(status().isCreated()) // == status().is(201)
				.andExpect(jsonPath("id").exists())
				.andExpect(header().exists(HttpHeaders.LOCATION))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
				.andExpect(jsonPath("free").value(false))
				.andExpect(jsonPath("offline").value(true))
				.andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.query-events").exists())
				.andExpect(jsonPath("_links.update-event").exists())
				//.andExpect(jsonPath("_links.profile").exists())
				.andDo(document("create-event",
						links(
								linkWithRel("self").description("link to self"),
								linkWithRel("query-events").description("link to query events"),
								linkWithRel("update-event").description("link to update an existing event"),
								linkWithRel("profile").description("link to description ")
						),
						requestHeaders(
								headerWithName(HttpHeaders.ACCEPT).description("accept header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
						),
						requestFields(
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("description of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
								fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
								fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
								fieldWithPath("endEventDateTime").description("date time of end of new event"),
								fieldWithPath("location").description("location of new event"),
								fieldWithPath("basePrice").description("base price of new event"),
								fieldWithPath("maxPrice").description("max price of new event"),
								fieldWithPath("limitOfEnrollment").description("limit of enrollment")
						),
						responseHeaders(
								headerWithName(HttpHeaders.LOCATION).description("location header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
						),
						// relaxedResponseFields를 쓰면 일부 response fields로 doc snipet을
						// 만들 수도 있겠지만, API 변경에 대응하지 못할 우려가 있으므로
						// 지양하자.
						responseFields(
								fieldWithPath("id").description("identifier of new event"),
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("description of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
								fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
								fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
								fieldWithPath("endEventDateTime").description("date time of end of new event"),
								fieldWithPath("location").description("location of new event"),
								fieldWithPath("basePrice").description("base price of new event"),
								fieldWithPath("maxPrice").description("max price of new event"),
								fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
								fieldWithPath("free").description("it tells if this event is free"),
								fieldWithPath("offline").description("it tells if this event is offline"),
								fieldWithPath("eventStatus").description("event status"),
								fieldWithPath("_links.self.href").description("link to self"),
								fieldWithPath("_links.query-events.href").description("link to query events"),
								fieldWithPath("_links.update-event.href").description("link to update event"),
								fieldWithPath("_links.profile.href").description("link to definition")

						)
				));


	}

	@Test
	@TestDescription("입력 받을 수 없는 값을 사용한 경우 에러가 발생하는 테스트")
	public void createEvent_BadRequest() throws Exception {
		Event event = Event.builder()
				.id(100)
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 8, 2, 2))
				.closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 9, 2, 2))
				.beginEventDateTime(LocalDateTime.of(2019, 12, 13, 2, 2))
				.endEventDateTime(LocalDateTime.of(2019, 12, 14, 2, 2))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타텁 팩토리")
				.free(true)
				.offline(false)
				.eventStatus(EventStatus.PUBLISHED)
				.build();

		mockMvc.perform(post("/api/events/")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaTypes.HAL_JSON)
					.content(objectMapper.writeValueAsString(event)))
				//print()를 통해 볼 수 있는 모든 내용을 andExpect로 확인 가능 합니다.
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Empty_Input() throws Exception {
		EventDto eventDto = EventDto.builder()
				.build();

		mockMvc.perform(post("/api/events/")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaTypes.HAL_JSON)
					.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@TestDescription("입력 값이 잘못 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Wrong_Input() throws Exception {
		EventDto eventDto = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 8, 2, 2))
				.closeEnrollmentDateTime(LocalDateTime.of(2019, 11, 9, 2, 2))
				.beginEventDateTime(LocalDateTime.of(2019, 12, 13, 2, 2))
				.endEventDateTime(LocalDateTime.of(2019, 10, 14, 2, 2))
				.basePrice(1000)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.build();

		mockMvc.perform(post("/api/events/")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("content.objectName").exists())
				.andExpect(jsonPath("content.defaultMessage").exists())
				.andExpect(jsonPath("content.code").exists())
				.andExpect(jsonPath("_links.index").exists());

	}

	@Test
	@TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	public void queryEvents() throws Exception {
		//Given
		IntStream.range(0, 30).forEach(this::generateEvent);

		//When
		mockMvc.perform(get("/api/events/")
							.param("page","1")
							.param("size","10")
							.param("sort", "name,DESC"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("page").exists())
				.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
				// Page 관련 링크 내용 설명, Request / Response 관련 내용들을 아래 나열한다.
				.andDo(document("query-events"));
		//Then
	}

	private void generateEvent(int i) {
		Event event = Event.builder()
				.name("event " + i)
				.build();

		eventRepository.save(event);
	}
}
