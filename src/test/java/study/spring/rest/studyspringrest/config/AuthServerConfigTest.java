package study.spring.rest.studyspringrest.config;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import study.spring.rest.studyspringrest.accounts.Account;
import study.spring.rest.studyspringrest.accounts.AccountRepository;
import study.spring.rest.studyspringrest.accounts.AccountRole;
import study.spring.rest.studyspringrest.accounts.AccountService;
import study.spring.rest.studyspringrest.common.BaseControllerTest;
import study.spring.rest.studyspringrest.common.TestDescription;

import java.util.Set;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;

	@Before
	public void setUp() {
		//예제에 언급되진 않았으나 인메모리 DB의 데이터 공유로
		//테스트 진행을 위해 설정해야 하는 코드
		this.accountRepository.deleteAll();
	}

	@Test
	@TestDescription("인증 토큰을 발급 받는 테스트")
	public void getAuthToken() throws Exception{
		//Given
		String username = "younsoo@naver.com";
		String password = "naver";
		Account younsoo = Account.builder()
				.email(username)
				.password(password)
				.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();
		accountService.saveAccount(younsoo);

		String clientId = "myApp";
		String clientSecret = "pass";

		this.mockMvc.perform(post("/oauth/token")
				.with(httpBasic(clientId, clientSecret))
				.param("username", username)
				.param("password", password)
				.param("grant_type", "password"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("access_token").exists());

	}
}