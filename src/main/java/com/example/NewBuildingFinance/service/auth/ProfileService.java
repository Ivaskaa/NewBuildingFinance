package com.example.NewBuildingFinance.service.auth;

import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.repository.auth.UserRepository;
import com.example.NewBuildingFinance.service.StaticService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Log4j2
@AllArgsConstructor
public class ProfileService implements UserDetailsService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final StaticService service;

    public User update(User userForm, MultipartFile file) throws IOException {
        log.info("update user: {}", userForm);
        User user = userRepository.findById(userForm.getId()).orElseThrow();
        user.setName(userForm.getName());
        user.setSurname(userForm.getSurname());
        user.setLastname(userForm.getLastname());
        user.setBirthday(userForm.getBirthday());
        user.setPhone(userForm.getPhone());
        user.setViber(userForm.getViber());
        user.setTelegram(userForm.getTelegram());
        user.setAboutMe(userForm.getAboutMe());
        user.setUsername(userForm.getUsername());
        if (userForm.getPassword()!= null) {
            user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        }
        String fileName;
        if(file != null && !file.getOriginalFilename().isEmpty()) {
            fileName = (UUID.randomUUID() + "." + file.getOriginalFilename());
            if(user.getPhoto() != null && !user.getPhoto().equals("")) {
                service.deletePhoto("users", user.getPhoto());
            }
            user.setPhoto(fileName);
            service.savePhoto("users", file, fileName);
        }
        userRepository.save(user);
        log.info("success");
        return user;
    }

    public User changeUserActive(User userForm) {
        log.info("change user active by id: {}", userForm.getId());
        User user = userRepository.findById(userForm.getId()).orElseThrow();
        user.setActive(!user.isActive());
        userRepository.save(user);
        log.info("success");
        return user;
    }

    public boolean checkEmail(User user) {
        return userRepository.findByUsername(user.getUsername()) != null;
    }

    public boolean checkPassword(User user, String password){
        if(password != null && !password.equals("")){
            if(passwordEncoder.matches(password, user.getPassword())){
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean checkEqualsPassword(String first, String second){
        return !first.equals(second);
    }

    public boolean checkPhone(String phone) {
        return phone.contains("_");
    }

    public boolean checkViber(String viber) {
        if(!viber.equals("+38(___)___-__-__")) {
            return viber.contains("_");
        }
        return false;
    }
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.info("User with username: {} not found", username);
            throw new UsernameNotFoundException("User not found");
        }
        log.info("User with username: {} found", username);
        return user;
    }
}
