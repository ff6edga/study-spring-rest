package study.spring.rest.studyspringrest.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;

	@Test
	public void findByUsername() {
		//Given
		String username = "younsookim@naver.com";
		String password = "younsoo";

		Account account = Account.builder()
				.email(username)
				.password(password)
				.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();

		accountRepository.save(account);

		//When
		UserDetailsService userDetailsService = accountService;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		//Then
		assertThat(userDetails.getPassword().equals(password));
	}

	//아래 테스트는 타입 정도만 확인 가능하다.
	//@Test(expected = UsernameNotFoundException.class)
	@Test
	public void findByUsernameFail() {
		String username = "random@email.com";
		try {
			accountService.loadUserByUsername(username);
			// 예외 미 발생시 테스트를 실패 시킨다
			fail("supposed to be failed");
		} catch (UsernameNotFoundException e) {
			// try-catch는 비교적 많은 내용을 확인 가능하지만 복잡하다
			assertThat(e instanceof UsernameNotFoundException).isTrue();
			assertThat(e.getMessage()).containsSequence(username);
		}
	}

	@Test
	public void findByUsernameFail_as_RULE() {
		//expected - 예측 코드를 먼저 작성해야 동작한다. 그래서 expected가 적절한 네이밍
		String username = "random@email.com";

		expectedException.expect(UsernameNotFoundException.class);
		expectedException.expectMessage(Matchers.containsString(username));

		//when
		accountService.loadUserByUsername(username);
	}
}