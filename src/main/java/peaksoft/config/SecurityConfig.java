package peaksoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetails(){
        User.UserBuilder userBuilder = User.withDefaultPasswordEncoder();

        UserDetails admin = userBuilder
                .username("azimbek")
                .password("azimbek12")
                .roles("ADMIN")
                .build();

        UserDetails doctor = userBuilder
                .username("uluk")
                .password("uluk12")
                .roles("DOCTOR")
                .build();

        UserDetails patient = userBuilder
                .username("maha")
                .password("maha12")
                .roles("PATIENT")
                .build();

        return new InMemoryUserDetailsManager(admin,doctor,patient);
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()

                .requestMatchers("/hospitals/").permitAll()
                .requestMatchers("/hospitals/new").hasRole("ADMIN")
                .requestMatchers("/hospitals/save").hasRole("ADMIN")
                .requestMatchers("/hospitals/{hospitalId}/delete").hasRole("ADMIN")
                .requestMatchers("/hospitals/edit/{id}").hasRole("ADMIN")
                .requestMatchers("/hospitals/{id}/update").hasRole("ADMIN")

                .requestMatchers("/doctors/{id}").hasAnyRole("ADMIN", "PATIENT")
                .requestMatchers("/doctors/{id}/newDoctor").hasRole("ADMIN")
                .requestMatchers("/doctors/{id}/saveDoctor").hasRole("ADMIN")
                .requestMatchers("/doctors/{id}/{hosId}").hasRole("ADMIN")
                .requestMatchers("/doctors/{id}/edit").hasRole("ADMIN")
                .requestMatchers("/doctors/{id}/{doctorId}/update").hasRole("ADMIN")
                .requestMatchers("/doctors/{id}/{doctorId}/assignDepartment").hasRole("ADMIN")

                .requestMatchers("/departments/{id}").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers("/departments/{id}/newDepartment").hasRole("ADMIN")
                .requestMatchers("/departments/{id}/saveDepartment").hasRole("ADMIN")
                .requestMatchers("/departments/edit/{id}").hasRole("ADMIN")
                .requestMatchers("/departments/{id}/{departmentId}/update").hasRole("ADMIN")
                .requestMatchers("/departments/{id}/{hosId}").hasRole("ADMIN")

                .requestMatchers("/patients/{id}").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers("/patients/{id}/newPatient").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/patients/{id}/savePatient").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/patients/edit/{id}").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/patients/{id}/{departmentId}/update").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/patients/{id}/{hosId}").hasAnyRole("ADMIN", "DOCTOR")

                .requestMatchers("/appointments/{id}").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers("/appointments/{id}/newAppointment").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers("/appointments/{id}/saveAppointment").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers("/appointments/edit/{id}").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers("/appointments/{id}/{appointmentId}/update").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .requestMatchers("/appointments/{id}/{hosId}").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                .and()
                .formLogin()
                .successHandler(new SimpleUrlAuthenticationSuccessHandler("/hospitals"))
                .permitAll();

        return http.build();
    }
}
