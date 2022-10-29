package com.example.NewBuildingFinance.service.auth;

import com.example.NewBuildingFinance.others.EmailContext;
import com.example.NewBuildingFinance.entities.auth.SecureToken;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.others.MailThread;
import com.example.NewBuildingFinance.repository.auth.SecureTokenRepository;
import com.example.NewBuildingFinance.repository.auth.UserRepository;
import com.example.NewBuildingFinance.service.MailService;
import com.example.NewBuildingFinance.service.SecureTokenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecureTokenService secureTokenService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MailService mailService;

    @Value("${site.base.url.http}")
    private String baseURL;

    public Page<User> findSortingPage(Integer currentPage, Integer size, String sortingField, String sortingDirection) {
        log.info("get object page: {}, field: {}, direction: {}", currentPage - 1, sortingField, sortingDirection);
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<User> userPage = userRepository.findAll(pageable);
        log.info("success");
        return userPage;
    }

    public List<User> findManagers() {
        log.info("get users where role permission manager");
        List<User> userPage = userRepository.findManagers();
        log.info("success");
        return userPage;
    }

    public User save(User user) {
        log.info("save user: {}", user);
        userRepository.save(user);
        log.info("success");
        sendRegistrationEmail(user); // create securityToken send email to User
        return user;
    }

    public User update(User userForm) {
        log.info("update user: {}", userForm);
        User user = userRepository.findById(userForm.getId()).orElseThrow();
        user.setName(userForm.getName());
        user.setSurname(userForm.getSurname());
        user.setRole(userForm.getRole());
        user.setPhone(userForm.getPhone());
        user.setUsername(userForm.getUsername());
        user.setActive(userForm.isActive());
        userRepository.save(user);
        log.info("success");
        return user;
    }

    public void deleteById(Long id) {
        log.info("delete user by id: {}", id);
        userRepository.deleteById(id);
        log.info("success");
    }

    public User findById(Long id) {
        log.info("get user by id: {}", id);
        User user = userRepository.findById(id).orElseThrow();
        log.info("success");
        return user;
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

    public User changeUserActiveById(Long id) {
        log.info("change user active by id: {}", id);
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(!user.isActive());
        userRepository.save(user);
        log.info("success");
        return user;
    }

    public boolean checkEmail(User user) {
        return userRepository.findByUsername(user.getUsername()) != null;
    }

    public boolean checkPhone(User user) {
        return user.getPhone().contains("_");
    }

    // registration from the user side
    public void sendRegistrationEmail(User user){
        SecureToken secureToken = secureTokenService.createSecureToken();
        secureToken.setUser(user);
        secureTokenService.save(secureToken);
        EmailContext emailContext = new EmailContext();
        emailContext.init(user);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        new MailThread(mailService, emailContext).start();
    }

    public User findUserByToken(String token) throws ParseException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if(secureToken != null) {
            Date out = Date.from(secureToken.getExpireAt().atZone(ZoneId.systemDefault()).toInstant());
            if (new Date().after(out)){
                deleteToken(token);
                return null;
            }
            return userRepository.findById(secureToken.getUser().getId()).orElse(null);
        }
        return null;
    }

    public void savePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        userRepository.save(user);
    }

    public void deleteToken(String token) {
        SecureToken secureToken = secureTokenService.findByToken(token);
        secureTokenService.delete(secureToken);
    }
}
