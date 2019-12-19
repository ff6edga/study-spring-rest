package study.spring.rest.studyspringrest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("event");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				.anonymous()
					.and()
				.authorizeRequests()
					.mvcMatchers(HttpMethod.GET, "/api/**").permitAll()
					.anyRequest().authenticated()
					.and()
				// 인증 실패 혹은 권한 없을 떄 생기는 예외에 대해 아래 핸들러를 쓰겠다.(403)
				.exceptionHandling()
					.accessDeniedHandler(new OAuth2AccessDeniedHandler());

	}
}
