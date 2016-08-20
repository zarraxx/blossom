package cn.net.xyan.blossom.platform;

import cn.net.xyan.blossom.core.support.SpringEntityManagerProviderFactory;
import cn.net.xyan.blossom.core.ui.BSideBar;
import cn.net.xyan.blossom.platform.annotation.BootstrapConfiguration;
import cn.net.xyan.blossom.platform.intercept.InterceptService;
import cn.net.xyan.blossom.platform.intercept.impl.InterceptServiceImpl;
import cn.net.xyan.blossom.platform.intercept.interceptor.LogInterceptor;
import cn.net.xyan.blossom.platform.service.*;
import cn.net.xyan.blossom.platform.service.impl.*;
import cn.net.xyan.blossom.platform.support.BlossomViewProvider;
import cn.net.xyan.blossom.platform.support.I18NMessageProviderImpl;
import cn.net.xyan.blossom.platform.ui.component.BSideBarUtils;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewService;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewServiceImpl;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
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

/**
 * Created by zarra on 16/5/13.
 */

@Configuration
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(value=BootstrapConfiguration.class,type= FilterType.ANNOTATION)})
@EnableAutoConfiguration(exclude = {
        BlossomBootstrapConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
       // EmbeddedServletContainerAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class})
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
@EnableVaadinSharedSecurity
@EnableTransactionManagement
public class BlossomConfiguration   {

    public static String RememberMeKey = "myAppKey";

    EntityManagerFactory emf;

    EntityManager entityManager;

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public DictService dictService() {
        return new DictServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityService securityService() {
        return new SecurityServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogService logService(){
        return new  LogServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public I18NMessageProviderImpl i18NMessageProvider(I18NService i18NService) {

        //ApplicationContextUtils.setServletContext(servletContext);

        I18NMessageProviderImpl provider = new I18NMessageProviderImpl();

        provider.setI18NService(i18NService);

        return provider;

    }

    @Bean
    @ConditionalOnMissingBean
    public BSideBarUtils bSideBarUtils(ApplicationContext applicationContext) {
        return new BSideBarUtils(applicationContext, null);
    }

    @Bean
    @UIScope
    public BSideBar bSideBar(BSideBarUtils sideBarUtils) {
        return new BSideBar(sideBarUtils);
    }


    @Bean
    @ConditionalOnMissingBean
    public EntityViewService entityViewService() {
        return new EntityViewServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public I18NService i18NService() {
        return new I18NServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public UISystemService uiSystemService() {
        return new UISystemServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public PlatformInfoService platformInfoService() {
        return new PlatformInfoServiceImpl();
    }

    @Bean
    public Filter jpaFilter() {
        return new org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringEntityManagerProviderFactory springEntityManagerProviderFactory() {
        SpringEntityManagerProviderFactory factory = new SpringEntityManagerProviderFactory();
        factory.setEntityManager(entityManager);
        return factory;
    }


    @Configuration
    public static class SecurityConfiguration extends WebSecurityConfigurerAdapter{
        @Autowired
        SecurityService securityService;

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("user").password("user").roles("USER")
//                .and().withUser("admin").password("admin").roles("ADMIN");
            auth.userDetailsService(securityService);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable(); // Use Vaadin's built-in CSRF protection instead
            http.authorizeRequests().antMatchers("/ui/login/**").anonymous()
                    .antMatchers("/login/**").anonymous()
                    .antMatchers("/ui/UIDL/**").permitAll()
                    .antMatchers("/ui/HEARTBEAT/**").permitAll()
                    .antMatchers("/vaadinServlet/UIDL/**").permitAll()
                    .antMatchers("/vaadinServlet/HEARTBEAT/**").permitAll()
                    .antMatchers("/public/**").permitAll()
                    .anyRequest().authenticated();

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

    @Configuration
    public static class ViewProviderConfiguration implements ApplicationContextAware,
            BeanDefinitionRegistryPostProcessor {

        private ApplicationContext applicationContext;
        private BeanDefinitionRegistry beanDefinitionRegistry;

        @Autowired
        private UISystemService uiSystemService;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext)
                throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public void postProcessBeanDefinitionRegistry(
                BeanDefinitionRegistry registry) throws BeansException {
            beanDefinitionRegistry = registry;
        }

        @Override
        public void postProcessBeanFactory(
                ConfigurableListableBeanFactory beanFactory) throws BeansException {
            // NOP
        }

        @Bean
        @ConditionalOnMissingBean
        public BlossomViewProvider blossomViewProvider() {
            BlossomViewProvider viewProvider =  new BlossomViewProvider(applicationContext, beanDefinitionRegistry);
            viewProvider.setUiSystemService(uiSystemService);
            return  viewProvider;
        }
    }

    @Configuration
    @EnableScheduling
    @EnableAsync
    public static class InterceptConfiguration implements ApplicationContextAware{

        //@Autowired
        //LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

        ApplicationContext ac;

        Logger logger = LoggerFactory.getLogger(InterceptConfiguration.class);

        @Bean
        public InterceptService interceptService( ){
            return new InterceptServiceImpl();
        }

        @Bean
        public LogInterceptor logInterceptor(){
            return new LogInterceptor();
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.ac = applicationContext;
        }
    }


}
