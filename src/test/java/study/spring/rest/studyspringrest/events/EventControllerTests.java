package study.spring.rest.studyspringrest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import study.spring.rest.studyspringrest.common.TestDescription;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

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
				.andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));

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
				.andExpect(jsonPath("$[0].objectName").exists())
//				.andExpect(jsonPath("$[0].field").exists())
				.andExpect(jsonPath("$[0].defaultMessage").exists())
				.andExpect(jsonPath("$[0].code").exists());
//				.andExpect(jsonPath("$[0].rejectedValue").exists());

	}
 }
