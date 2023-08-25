package Certis.Web.config;

import Certis.Web.auth.PrincipalDetailsService;
import Certis.Web.auth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired private PrincipalOauth2UserService principalOauth2UserService;
    @Autowired private PrincipalDetailsService principalDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()

                .and()					//추가
                .formLogin()				// form기반의 로그인인 경우
                .loginPage("/loginForm")		// 인증이 필요한 URL에 접근하면 /loginForm으로 이동
                .usernameParameter("email")		// 로그인 시 form에서 가져올 값
                .passwordParameter("password")		// 로그인 시 form에서 가져올 값
                .loginProcessingUrl("/login")		// 로그인을 처리할 URL 입력
                .defaultSuccessUrl("/index")			// 로그인 성공하면 "/index" 으로 이동
                .failureUrl("/loginForm")		//로그인 실패 시 /loginForm으로 이동

                .and()
                .logout().permitAll()					// logout

                .and()
                .oauth2Login()				// OAuth2기반의 로그인인 경우
                .loginPage("/login")		// 인증이 필요한 URL에 접근하면 /loginForm으로 이동
                .defaultSuccessUrl("/")			// 로그인 성공하면 "/" 으로 이동
                .failureUrl("/login")		// 로그인 실패 시 /loginForm으로 이동
                .userInfoEndpoint()			// 로그인 성공 후 사용자정보를 가져온다
                .userService(principalOauth2UserService);	//사용자정보를 처리할 때 사용한다
    }
}
