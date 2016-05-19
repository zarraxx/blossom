package cn.net.xyan.blossom.platform;

import cn.net.xyan.blossom.core.jpa.support.EasyJpaRepositoryFactoryBean;
import cn.net.xyan.blossom.core.support.LazyHibernateFilter;
import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import org.springframework.boot.orm.jpa.EntityScan;
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
import org.vaadin.spring.http.HttpService;
import org.vaadin.spring.security.annotation.EnableVaadinSharedSecurity;
import org.vaadin.spring.security.config.VaadinSharedSecurityConfiguration;
import org.vaadin.spring.security.shared.VaadinAuthenticationSuccessHandler;
import org.vaadin.spring.security.shared.VaadinSessionClosingLogoutHandler;
import org.vaadin.spring.security.shared.VaadinUrlAuthenticationSuccessHandler;
import org.vaadin.spring.security.web.VaadinRedirectStrategy;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Created by zarra on 16/5/13.
 */

@Configuration
@ComponentScan
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
@EnableVaadinSharedSecurity
@EntityScan(
        basePackages = {
                "cn.net.xyan.blossom.core.jpa.entity",
                "cn.net.xyan.blossom.platform.entity"})
@EnableJpaRepositories(
        basePackages = {
                "cn.net.xyan.blossom.core.jpa.dao",
                "cn.net.xyan.blossom.platform.dao"},
        repositoryFactoryBeanClass = EasyJpaRepositoryFactoryBean.class
)

@EnableTransactionManagement
public class BlossomConfiguration extends WebSecurityConfigurerAdapter {

    public static String RememberMeKey = "myAppKey";

    EntityManagerFactory emf;

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("user").roles("USER")
                .and().withUser("admin").password("admin").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); // Use Vaadin's built-in CSRF protection instead
        http.authorizeRequests().antMatchers("/login/**").anonymous()
                .antMatchers("/vaadinServlet/UIDL/**").permitAll()
                .antMatchers("/vaadinServlet/HEARTBEAT/**").permitAll()
                .anyRequest().authenticated();

        http.httpBasic().disable();
        http.formLogin().disable();
        // Remember to add the VaadinSessionClosingLogoutHandler
        http.logout().addLogoutHandler(new VaadinSessionClosingLogoutHandler()).logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout").permitAll();
        http.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));
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

    /**
     * The {@link AuthenticationManager} must be available as a Spring bean for Vaadin4Spring.
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * The {@link RememberMeServices} must be available as a Spring bean for Vaadin4Spring.
     */
    @Bean
    public RememberMeServices rememberMeServices() {
        return new TokenBasedRememberMeServices(RememberMeKey, userDetailsService());
    }

    /**
     * The {@link SessionAuthenticationStrategy} must be available as a Spring bean for Vaadin4Spring.
     */
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new SessionFixationProtectionStrategy();
    }

    @Bean(name = VaadinSharedSecurityConfiguration.VAADIN_AUTHENTICATION_SUCCESS_HANDLER_BEAN)
    VaadinAuthenticationSuccessHandler vaadinAuthenticationSuccessHandler(HttpService httpService,
                                                                          VaadinRedirectStrategy vaadinRedirectStrategy) {
        return new VaadinUrlAuthenticationSuccessHandler(httpService, vaadinRedirectStrategy, "/");
    }

    @Bean
    LazyHibernateFilter lazyHibernateFilter(){
        LazyHibernateFilter lazyHibernateFilter = new LazyHibernateFilter();
        lazyHibernateFilter.setEmf(getEmf());
        return lazyHibernateFilter;
    }

    @Bean
    ApplicationContextUtils applicationContextUtils(){
        return new ApplicationContextUtils();
    }

}