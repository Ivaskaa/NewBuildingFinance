package com.example.NewBuildingFinance.config;

import com.example.NewBuildingFinance.service.auth.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("/registration/**").permitAll()
                    .antMatchers("/profile/**").permitAll()
                    .antMatchers("/statistics/**").hasAuthority("STATISTICS")
                    .antMatchers("/flats/**").hasAuthority("FLATS")
                    .antMatchers("/cashRegister/**").hasAuthority("CASH_REGISTER")
                    .antMatchers("/buyers/**").hasAuthority("BUYERS")
                    .antMatchers("/agencies/**").hasAuthority("AGENCIES")
                    .antMatchers("/objects/**").hasAuthority("OBJECTS")
                    .antMatchers("/contracts/**").hasAuthority("CONTRACTS")
                    .antMatchers("/settings/**").hasAuthority("SETTINGS")
                    .antMatchers("/api/v1/auth/**").hasAuthority("API")
                .and()
                    //Настройка для входа в систему
                    .formLogin()
                    .loginPage("/login")
                    //Перенарпавление на главную страницу после успешного входа
                    .defaultSuccessUrl("/profile")
                    .permitAll()
                .and()
                    .logout()
                    .permitAll()
                    .logoutSuccessUrl("/login")
                    .deleteCookies("JSESSIONID") // не можна видаляти при remember me
                .and()
                    .rememberMe()
                    .key("uniqueAndSecret").tokenValiditySeconds(1209600); // 2 тижні
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }
}
