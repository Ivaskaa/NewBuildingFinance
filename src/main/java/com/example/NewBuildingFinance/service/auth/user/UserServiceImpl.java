package com.example.NewBuildingFinance.service.auth.user;

import com.example.NewBuildingFinance.entities.auth.Permission;
import com.example.NewBuildingFinance.others.mail.MailThread;
import com.example.NewBuildingFinance.others.mail.context.EmailContextUserRegistration;
import com.example.NewBuildingFinance.entities.auth.SecureToken;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.repository.auth.UserRepository;
import com.example.NewBuildingFinance.service.mail.MailServiceImpl;
import com.example.NewBuildingFinance.service.secureToken.SecureTokenServiceImpl;
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

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserServiceImpl implements UserDetailsService, UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecureTokenServiceImpl secureTokenServiceImpl;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MailServiceImpl mailServiceImpl;

    @Value("${site.base.url.http}")
    private String baseURL;

    @Override
    public Page<User> findSortingPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection) {
        log.info("get users page: {}, field: {}, direction: {}", currentPage - 1, sortingField, sortingDirection);
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<User> userPage = userRepository.findAllByDeletedFalse(pageable);
        log.info("success");
        return userPage;
    }

    @Override
    public User findDirector() {
        log.info("get director");
        User user = userRepository.findById(1L).orElseThrow();
        log.info("success get director");
        return user;
    }

    @Override
    public List<User> findManagers(Long userId) {
        log.info("get users where role permission buyers");
        List<User> userPage = userRepository.findManagers(userId);
        System.out.println(userPage);
        log.info("success");
        return userPage;
    }

    @Override
    public User save(User user) {
        log.info("save user: {}", user);
        userRepository.save(user);
        sendRegistrationEmail(user); // create securityToken send email to User
        log.info("success");
        return user;
    }

    @Override
    public User update(User userForm) {
        log.info("update user: {}", userForm);
        User user = userRepository.findById(userForm.getId()).orElseThrow();
        user.setName(userForm.getName());
        user.setSurname(userForm.getSurname());
        user.setRole(userForm.getRole());
        user.setPhone(userForm.getPhone());
        user.setUsername(userForm.getUsername());
        userRepository.save(user);
        log.info("success");
        return user;
    }

    @Override
    public void deleteById(Long id) {
        log.info("delete user by id: {}", id);
        userRepository.setDeleted(id);
        log.info("success");
    }

    @Override
    public User findById(Long id) {
        log.info("get user by id: {}", id);
        User user = userRepository.findById(id).orElseThrow();
        log.info("success");
        return user;
    }

    @Override
    public List<String> getUserPermissionsById(Long id) {
        log.info("get user permissions by id: {}", id);
        //
        User user = userRepository.findById(id).orElseThrow();
        List<String> permissions = user.getRole().getPermissions()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
        log.info("success");
        return permissions;
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

    @Override
    public User changeUserActiveById(Long id) {
        log.info("change user active by id: {}", id);
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(!user.isActive());
        userRepository.save(user);
        log.info("success");
        return user;
    }

    @Override
    public boolean checkEmail(String email) {
        return userRepository.findByUsername(email) != null;
    }

    @Override
    public boolean checkPhone(String phone) {
        return phone.contains("_");
    }


    // registration from the user side
    @Override
    public void sendRegistrationEmail(User user){
        SecureToken secureToken = secureTokenServiceImpl.createSecureToken();
        secureToken.setUser(user);
        secureTokenServiceImpl.save(secureToken);
        EmailContextUserRegistration emailContext = new EmailContextUserRegistration();
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        emailContext.setTemplateLocation("email/email-registration");
        emailContext.setSubject("Complete your registration");
        emailContext.setFrom("no-reply@javadevjournal.com");
        emailContext.setTo(user.getUsername());
        emailContext.setUser(user.getSurname() + " " + user.getName());
        new MailThread(mailServiceImpl, emailContext).start();
    }

    @Override
    public User findUserByToken(String token) {
        SecureToken secureToken = secureTokenServiceImpl.findByToken(token);
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

    @Override
    public void savePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public void deleteToken(String token) {
        SecureToken secureToken = secureTokenServiceImpl.findByToken(token);
        secureTokenServiceImpl.delete(secureToken);
    }
}
