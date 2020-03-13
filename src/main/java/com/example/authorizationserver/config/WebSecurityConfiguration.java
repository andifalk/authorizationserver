package com.example.authorizationserver.config;

import com.example.authorizationserver.security.client.RegisteredClientDetailsService;
import com.example.authorizationserver.security.user.EndUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
public class WebSecurityConfiguration {

  @Configuration
  @Order(101)
  public static class PublicEndpoints extends WebSecurityConfigurerAdapter {

    private final RegisteredClientDetailsService registeredClientDetailsService;

    public PublicEndpoints(@Autowired @Qualifier("registeredClientDetailsService") RegisteredClientDetailsService registeredClientDetailsService) {
      this.registeredClientDetailsService = registeredClientDetailsService;
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
                a.mvcMatchers(POST, "/token").permitAll();
                a.mvcMatchers(GET, "/.well-known/openid-configuration", "/jwks", "/userinfo")
                    .permitAll();
                a.anyRequest().denyAll();
              })
          .httpBasic(withDefaults())
          .userDetailsService(this.registeredClientDetailsService)
          .csrf()
          .disable();
    }
  }

  @Configuration
  @Order(102)
  public static class ApiEndpoints extends WebSecurityConfigurerAdapter {

    private final EndUserDetailsService endUserDetailsService;

    public ApiEndpoints(
        @Autowired @Qualifier("endUserDetailsService")
            EndUserDetailsService endUserDetailsService) {
      this.endUserDetailsService = endUserDetailsService;
    }

    protected void configure(HttpSecurity http) throws Exception {
      http.requestMatchers(r -> r.mvcMatchers("/api/*"))
          .authorizeRequests(
              a -> {
                a.mvcMatchers("/api/*").hasRole("ADMIN");
                a.anyRequest().denyAll();
              })
          .httpBasic(withDefaults())
          .formLogin(withDefaults())
          .userDetailsService(this.endUserDetailsService)
          .csrf()
          .disable();
    }
  }

  @Configuration
  @Order(103)
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
          .formLogin(withDefaults())
          .userDetailsService(this.endUserDetailsService);
    }
  }
}
