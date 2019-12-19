package study.spring.rest.studyspringrest.events;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import study.spring.rest.studyspringrest.accounts.Account;
import study.spring.rest.studyspringrest.accounts.AccountRepository;
import study.spring.rest.studyspringrest.accounts.AccountRole;
import study.spring.rest.studyspringrest.accounts.AccountService;
import study.spring.rest.studyspringrest.common.AppProperties;
import study.spring.rest.studyspringrest.common.BaseControllerTest;
import study.spring.rest.studyspringrest.common.TestDescription;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {

	@Autowired
	EventRepository eventRepository;

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AppProperties appProperties;

	@Before
	public void setUp() {
		//테스트 마다 인메모리DB 내용이 공유된다.
		this.accountRepository.deleteAll();
		this.eventRepository.deleteAll();
	}

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
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
						relaxedResponseFields(
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

	private String getBearerToken() throws Exception {
		return "Bearer" + getAccessToken();
	}

	private String getAccessToken() throws Exception {
		//Given
		Account younsoo = Account.builder()
				.email(appProperties.getUserUsername())
				.password(appProperties.getUserPassword())
				.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();
		accountService.saveAccount(younsoo);

		ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/oauth/token")
				.with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
				.param("username", appProperties.getUserUsername())
				.param("password", appProperties.getUserPassword())
				.param("grant_type", "password"));

		var responseBody = perform.andReturn().getResponse().getContentAsString();
		JsonParser jsonParser = new Jackson2JsonParser();

		return jsonParser.parseMap(responseBody).get("access_token").toString();

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
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("content[0].objectName").exists())
				.andExpect(jsonPath("content[0].defaultMessage").exists())
				.andExpect(jsonPath("content[0].code").exists())
				.andExpect(jsonPath("_links.index").exists());

	}

	@Test
	@TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	public void queryEvents() throws Exception {
		//Given
		IntStream.range(0, 30).forEach(this::generateEvent);

		//When
		ResultActions perform = mockMvc.perform(get("/api/events/")
				.param("page", "1")
				.param("size", "10")
				.param("sort", "name,DESC"));

		//Then
		perform.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("page").exists())
				.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
				// Page 관련 링크 내용 설명, Request / Response 관련 내용들을 아래 나열한다.
				.andDo(document("query-events"));
	}

	@Test
	@TestDescription("기존의 이벤트를 하나 조회하기")
	public void getEvent() throws Exception {
		// Given
		Event event = this.generateEvent(100);

		//When & Then
		mockMvc.perform(get("/api/events/{id}", event.getId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").exists())
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
				// 물론 아래에 문서화들을 진행해야 하는 내용들이다.
				.andDo(document("get-an-event"));
	}

	@Test
	@TestDescription("이벤트를 정상적으로 수정하기")
	public void updateEvent() throws Exception {
		//Given
		Event event = this.generateEvent(100);
		EventDto eventDto = modelMapper.map(event, EventDto.class);
		eventDto.setName("Updated Event");

		mockMvc.perform(put("/api/events/{id}", event.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").value(eventDto.getName()))
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
				//당연히 아래에 추가로 문서화 해야하는 부분
				.andDo(document("update-an-event"));
	}

	@Test
	@TestDescription("입력값이 비어있는 경우에 이벤트 수정 실패")
	public void updateEvent_404_empty() throws Exception {
		//Given
		Event event = this.generateEvent(100);
		EventDto eventDto = new EventDto();

		//When
		mockMvc.perform(put("/api/events/{id}", event.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
	public void updateEvent_404_wrong() throws Exception {
		//Given
		Event event = this.generateEvent(100);
		EventDto eventDto = modelMapper.map(event, EventDto.class);
		eventDto.setBasePrice(500000);
		eventDto.setMaxPrice(200);

		//When
		mockMvc.perform(put("/api/events/{id}", event.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@TestDescription("존재하지 않는 이벤트 수정 실패")
	public void updateEvent_404_not_exist() throws Exception {
		//Given
		Event event = this.generateEvent(100);
		EventDto eventDto = modelMapper.map(event, EventDto.class);
		eventDto.setName("Updated Event");

		mockMvc.perform(put("/api/events/31224")
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	@TestDescription("없는 이벤트는 조회했을 때 404 응답받기")
	public void getEvent_404() throws Exception {
		//When & Then
		mockMvc.perform(get("/api/events/31224"))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	private Event generateEvent(int index) {
		Event event = Event.builder()
				.name("event " + index)
				.description("Test Event")
				.beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 8, 2, 2))
				.closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 9, 2, 2))
				.beginEventDateTime(LocalDateTime.of(2019, 12, 13, 2, 2))
				.endEventDateTime(LocalDateTime.of(2019, 12, 14, 2, 2))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타텁 팩토리")
				.free(false)
				.offline(true)
				.eventStatus(EventStatus.DRAFT)
				.build();

		return eventRepository.save(event);
	}
}
