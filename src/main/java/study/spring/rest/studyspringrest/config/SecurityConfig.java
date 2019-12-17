package study.spring.rest.studyspringrest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

	//같은 일을 하지만 상대적으론 Spring Security가 적용되므로 비효율적인 방법이다.
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		//일단 Spring Security는 적용되고 있는 상태다. (Security 안으로 들어와서 Filter chain을 타게 된다)
//		http.authorizeRequests()
//				.mvcMatchers("/docs/index.html").anonymous()
//				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).anonymous();
//	}
}
