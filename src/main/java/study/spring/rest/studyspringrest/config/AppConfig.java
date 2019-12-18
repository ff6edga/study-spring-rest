package study.spring.rest.studyspringrest.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.spring.rest.studyspringrest.accounts.Account;
import study.spring.rest.studyspringrest.accounts.AccountRole;
import study.spring.rest.studyspringrest.accounts.AccountService;

import java.util.Set;

@Configuration
public class AppConfig {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public ApplicationRunner applicationRunner() {
		return new ApplicationRunner() {

			@Autowired
			AccountService accountService;

			@Override
			public void run(ApplicationArguments args) throws Exception {
				Account younsoo = Account.builder()
						.email("younsoo@naver.com")
						.password("naver")
						.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
						.build();
				accountService.saveAccount(younsoo);
			}
		};
	}
}
