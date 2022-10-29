package com.example.NewBuildingFinance.entities.auth;

import com.example.NewBuildingFinance.dto.auth.ProfileDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String surname;
    private String lastname;
    private Date birthday;
    private String phone;
    private String viber;
    private String telegram;
    private String aboutMe;
    private String photo;
    private String username; // email
    private String password;
    private boolean active;
    @JoinColumn(name = "role_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Role role;

    @JoinColumn(name = "user_id")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonBackReference
    private Set<SecureToken> secureTokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getPermissions()
                .stream()
                .map(role -> (new SimpleGrantedAuthority(role.getName())))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", birthday=" + birthday +
                ", phone='" + phone + '\'' +
                ", viber='" + viber + '\'' +
                ", telegram='" + telegram + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", photo='" + photo + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                ", role=" + role +
                '}';
    }

//    public ProfileDto buildProfile() throws ParseException {
//        ProfileDto profile = new ProfileDto();
//        profile.setId(id);
//        profile.setName(name);
//        profile.setSurname(surname);
//        if(!lastname.equals("")) {
//            profile.setLastname(lastname);
//        }
//        if(birthday!= null) {
//            profile.setBirthday(birthday.toString());
//        }
//        profile.setPhone(phone);
//        if(viber != null && !viber.equals("")){
//            profile.setViber(viber);
//        }
//        if(telegram != null && !telegram.equals("")) {
//            profile.setTelegram(telegram);
//        }
//        if(aboutMe != null && !aboutMe.equals("")) {
//            profile.setAboutMe(aboutMe);
//        }
//        profile.setUsername(username);
//        return profile;
//    }
}
