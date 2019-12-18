package study.spring.rest.studyspringrest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import study.spring.rest.studyspringrest.accounts.AccountService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// @EnableWebSecurity와 WebSecurityConfigureAdapter를 상속받는 순간
	// 스프링 부트가 제공해주는 기본 시큐리티 설정을 포기하며 여기서 구현한 것들을
	// 사용하겠다는 의미가 된다.

	@Autowired
	AccountService accountService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	//다른 AuthorizationServer나 ResourceServer가 참조할 수 있도록 Bean으로 노출 시킨다.
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(accountService)
				.passwordEncoder(passwordEncoder);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		//Spring Security를 적용하기도 전에 걸러진다.
		web.ignoring().mvcMatchers("/docs/index.html");
		web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.anonymous().and() // 익명 사용자를 허용할 것이고
				.formLogin().and() // 폼 인증을 사용할 것이고
				.authorizeRequests() // 허용할 요청들은
					// /api/ 뒤에 모든 GET 요청들이다.
					.mvcMatchers(HttpMethod.GET, "/api/**").authenticated()
					// 나머지 요청들은 인증이 필요하다.
					.anyRequest().authenticated();
	}
}
