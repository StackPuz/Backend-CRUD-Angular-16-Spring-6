package app.config;

import app.util.Util;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@PropertySource("classpath:application.properties")
public class WebSecurityConfig {
    
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter ;

    @Autowired
    private Environment env;

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        Locale.setDefault(Locale.ROOT);
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        http.addFilterBefore(filter,CsrfFilter.class)
        .csrf().disable()
        .cors().configurationSource(new CorsConfigurationSource() {
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
                config.addAllowedMethod(HttpMethod.PUT);
                config.addAllowedMethod(HttpMethod.DELETE);
                return config;
            }
        }).and()
        .authorizeHttpRequests()
        .requestMatchers("/uploads/**", "/api/login", "/api/logout", "/api/resetPassword", "/api/changePassword/**", "/api/stack").permitAll()
        .anyRequest().authenticated()
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);
        Util.setProperties(env);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/uploads/**", "/api/login", "/api/logout", "/api/resetPassword", "/api/changePassword/**", "/api/stack");
    }

    @Bean
    public String jwtSecret() {
        return jwtSecret;
    }

    @Bean
    public UserDetailsService userDetailsService()
    {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager();
        userDetailsManager.setDataSource(dataSource);
        userDetailsManager.setUsersByUsernameQuery("SELECT name as username, password, active as enabled FROM UserAccount where name = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery("SELECT UserAccount.name as username, Role.name as authority FROM UserAccount join UserRole on UserAccount.id = UserRole.user_id join Role on UserRole.role_id = Role.id where UserAccount.name = ?");
        return userDetailsManager;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}