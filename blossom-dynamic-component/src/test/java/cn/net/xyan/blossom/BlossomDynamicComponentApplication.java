package cn.net.xyan.blossom;

import com.vaadin.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.vaadin.spring.http.HttpService;
import org.vaadin.spring.security.annotation.EnableVaadinSharedSecurity;
import org.vaadin.spring.security.config.VaadinSharedSecurityConfiguration;
import org.vaadin.spring.security.shared.VaadinAuthenticationSuccessHandler;
import org.vaadin.spring.security.shared.VaadinSessionClosingLogoutHandler;
import org.vaadin.spring.security.shared.VaadinUrlAuthenticationSuccessHandler;
import org.vaadin.spring.security.web.VaadinRedirectStrategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.servlet.Filter;

@SpringBootApplication
public class BlossomDynamicComponentApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlossomDynamicComponentApplication.class, args);
	}


	@Configuration
	@ComponentScan
	@EnableWebSecurity
	@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
	@EnableVaadinSharedSecurity
	@EnableTransactionManagement
	public static class BlossomConfiguration extends WebSecurityConfigurerAdapter {

		public static String RememberMeKey = "myAppKey";


		@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication()
					.withUser("user").password("user").roles("USER")
					.and().withUser("admin").password("admin").roles("ADMIN");
			//auth.userDetailsService(securityService);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable(); // Use Vaadin's built-in CSRF protection instead
			http.authorizeRequests()
					//.antMatchers("/ui/login/**").anonymous()
					//.antMatchers("/login/**").anonymous()
					//.antMatchers("/ui/UIDL/**").permitAll()
					//.antMatchers("/ui/HEARTBEAT/**").permitAll()
					//.antMatchers("/vaadinServlet/UIDL/**").permitAll()
					//.antMatchers("/vaadinServlet/HEARTBEAT/**").permitAll()
					//.antMatchers("/public/**").permitAll()
					.anyRequest().permitAll();

			http.httpBasic().disable();
			http.formLogin().disable();
			// Remember to add the VaadinSessionClosingLogoutHandler
			http.logout().addLogoutHandler(new VaadinSessionClosingLogoutHandler()).logoutUrl("/logout")
					.logoutSuccessUrl("/ui/login?logout").permitAll();
			http.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/ui/login"));
			// Instruct Spring Security to use the same RememberMeServices as Vaadin4Spring. Also remember the key.
			http.rememberMe().rememberMeServices(rememberMeServices()).key(RememberMeKey);
			// Instruct Spring Security to use the same authentication strategy as Vaadin4Spring
			http.sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy());

			http.headers().disable();
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/VAADIN/**");
		}


		@Override
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}


		@Bean
		public RememberMeServices rememberMeServices() {
			return new TokenBasedRememberMeServices(RememberMeKey, userDetailsService());
		}

		@Bean
		public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
			return new SessionFixationProtectionStrategy();
		}

		@Bean(name = VaadinSharedSecurityConfiguration.VAADIN_AUTHENTICATION_SUCCESS_HANDLER_BEAN)
		VaadinAuthenticationSuccessHandler vaadinAuthenticationSuccessHandler(HttpService httpService,
																			  VaadinRedirectStrategy vaadinRedirectStrategy) {
			return new VaadinUrlAuthenticationSuccessHandler(httpService, vaadinRedirectStrategy, "/");
		}


		@Configuration
		public static class StaticResourceConfiguration extends WebMvcConfigurerAdapter {

			private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
					"classpath:/META-INF/resources/", "classpath:/resources/",
					"classpath:/static/", "classpath:/public/"};

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
			}
		}


	}
}
