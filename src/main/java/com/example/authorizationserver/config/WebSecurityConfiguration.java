package com.example.authorizationserver.config;

import com.example.authorizationserver.security.client.RegisteredClientDetailsService;
import com.example.authorizationserver.security.user.EndUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.Customizer.withDefaults;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Configuration
    @Order(1)
    public static class PublicEndpoints extends WebSecurityConfigurerAdapter {

        private final RegisteredClientDetailsService registeredClientDetailsService;

        public PublicEndpoints(@Autowired @Qualifier("registeredClientDetailsService") RegisteredClientDetailsService registeredClientDetailsService) {
            this.registeredClientDetailsService = registeredClientDetailsService;
        }

        @Override
        public void configure(WebSecurity web) {
            web.ignoring().mvcMatchers("/token", "/introspect",
                    "/revoke", "/userinfo", "/.well-known/openid-configuration", "/jwks");
        }

        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers(
                    r ->
                            r.mvcMatchers(
                                    "/token",
                                    "/introspect",
                                    "/revoke",
                                    "/userinfo",
                                    "/.well-known/openid-configuration",
                                    "/jwks"))
                    .authorizeRequests(
                            a -> {
                                a.mvcMatchers(POST, "/introspect").hasRole("CLIENT");
                                a.mvcMatchers(POST, "/revoke").hasRole("CLIENT");
                                a.mvcMatchers(POST, "/token").hasRole("CLIENT");
                                a.anyRequest().denyAll();
                            })
                    .httpBasic(withDefaults())
                    .userDetailsService(this.registeredClientDetailsService)
                    .cors(withDefaults())
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(2)
    public static class ApiEndpoints extends WebSecurityConfigurerAdapter {

        private final EndUserDetailsService endUserDetailsService;

        public ApiEndpoints(
                @Autowired @Qualifier("endUserDetailsService")
                        EndUserDetailsService endUserDetailsService) {
            this.endUserDetailsService = endUserDetailsService;
        }

        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers(r -> r.mvcMatchers("/api/**"))
                    .authorizeRequests(
                            a -> {
                                a.mvcMatchers("/api/**").hasRole("ADMIN");
                                a.anyRequest().denyAll();
                            })
                    .httpBasic(withDefaults())
                    //.formLogin(withDefaults())
                    .userDetailsService(this.endUserDetailsService)
                    .cors(withDefaults())
                    .csrf()
                    .disable();
        }
    }

    @Configuration
    @Order(3)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final EndUserDetailsService endUserDetailsService;

        public FormLoginWebSecurityConfigurerAdapter(
                @Autowired @Qualifier("endUserDetailsService")
                        EndUserDetailsService endUserDetailsService) {
            this.endUserDetailsService = endUserDetailsService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests(authorize -> authorize.anyRequest().authenticated())
                    .cors(withDefaults())
                    .formLogin(withDefaults())
                    .userDetailsService(this.endUserDetailsService)
                    .headers().contentSecurityPolicy(
                    csp -> csp.policyDirectives(
                            "upgrade-insecure-requests; default-src 'self' https:; " +
                                    "style-src 'self' stackpath.bootstrapcdn.com maxcdn.bootstrapcdn.com getbootstrap.com; " +
                                    "script-src code.jquery.com cdnjs.cloudflare.com " +
                                    "stackpath.bootstrapcdn.com;" +
                                    "font-src 'self' data:;" +
                                    "object-src to 'none'")
            );
        }
    }
}
