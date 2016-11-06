package com.viktorban.wlgame.config;

import com.viktorban.wlgame.model.User;
import com.viktorban.wlgame.model.UserWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Configures everything security-related.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Returns the password encoder bean.
     *
     * @return The password encoder bean.
     */
    @Bean(name = "passwordEncoder")
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Custom UserDetailsService implementation to use User entities for authentication.
     *
     * @see com.viktorban.wlgame.model.User
     * @see com.viktorban.wlgame.model.UserWrapper
     */
    @Service
    public class UserDetailsServiceImpl implements UserDetailsService {

        /**
         * JPA entity manager.
         */
        @PersistenceContext
        private EntityManager entityManager;

        /**
         * {@inheritDoc}
         */
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            try {
                User user = (User)entityManager
                        .createQuery("SELECT u FROM com.viktorban.wlgame.model.User u WHERE u.name = :username")
                        .setParameter("username", username)
                        .getSingleResult();
                return new UserWrapper(user);
            }
            catch (Exception e) {
                throw new UsernameNotFoundException("Username not found.", e);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/*").permitAll()
                    .antMatchers("/api/login").permitAll()
                    .antMatchers("/api/logout").permitAll()
                    .antMatchers("/api/**").authenticated()
                    .and()
                .formLogin()
                    .loginPage("/api/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(new SimpleUrlAuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                            // Don't redirect with 301, return with 200 instead.
                            clearAuthenticationAttributes(request);
                        }
                    })
                    .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                    .permitAll()
                    .and()
                .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .and()
                .exceptionHandling()
                    // Instead of redirecting to /api/login, return with a 401 error code.
                    .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
        ;
    }

}
