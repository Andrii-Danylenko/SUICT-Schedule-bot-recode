package rozkladbot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private static final String LOGIN_ENDPOINT = "login";
    private static final String SCHEDULE_ENDPOINT = "/schedule";
    private static final String FACULTIES_ENDPOINT = SCHEDULE_ENDPOINT + "/faculties";
    private static final String COURSES_ENDPOINT = SCHEDULE_ENDPOINT + "/courses";
    private static final String GROUPS_ENDPOINT = SCHEDULE_ENDPOINT + "/groups";
    @Value("${server.link.1}")
    private static String SERVER_LINK_1;
    @Value("${server.link.1}")
    private static String SERVER_LINK_2;

    @Autowired
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList(SERVER_LINK_1));
                    config.setAllowedOrigins(Collections.singletonList(SERVER_LINK_2));
                    config.setAllowedMethods(
                            List.of("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(HttpMethod.POST, LOGIN_ENDPOINT, SCHEDULE_ENDPOINT + "/get")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET,
                                        LOGIN_ENDPOINT,
                                        SCHEDULE_ENDPOINT + "/get",
                                        SCHEDULE_ENDPOINT,
                                        FACULTIES_ENDPOINT,
                                        GROUPS_ENDPOINT,
                                        COURSES_ENDPOINT)
                                .permitAll()
                                .requestMatchers("/css/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated());
        return http.build();
    }
}
