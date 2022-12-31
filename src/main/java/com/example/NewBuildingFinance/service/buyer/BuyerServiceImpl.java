package com.example.NewBuildingFinance.service.buyer;

import com.example.NewBuildingFinance.dto.buyer.BuyerSaveDto;
import com.example.NewBuildingFinance.dto.buyer.BuyerTableDto;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.buyer.DocumentStyle;
import com.example.NewBuildingFinance.others.mail.MailThread;
import com.example.NewBuildingFinance.others.mail.context.EmailContextBuyerPassword;
import com.example.NewBuildingFinance.repository.BuyerRepository;
import com.example.NewBuildingFinance.repository.auth.PermissionRepository;
import com.example.NewBuildingFinance.service.mail.MailService;
import com.example.NewBuildingFinance.service.staticService.StaticService;
import com.stripe.exception.StripeException;
import lombok.extern.log4j.Log4j2;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.passay.CharacterCharacteristicsRule.ERROR_CODE;

@Service
@Log4j2
public class BuyerServiceImpl implements BuyerService {
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private StaticService staticService;
    @Autowired
    private MailService mailService;
    @Autowired
    private PermissionRepository permissionRepository;
//    @Value("${stripe.apikey}")
//    private String stripeKey;

    @Override
    public Page<BuyerTableDto> findSortingPage(
            @NotNull Integer currentPage,
            @NotNull Integer size,
            @NotNull String sortingField,
            @NotNull String sortingDirection) {
        log.info("get buyer page: {}, field: {}, direction: {}", currentPage - 1, sortingField, sortingDirection);
        Sort sort;
        if (sortingField.contains(" and ")) {
            sort = staticService.sort(sortingField, sortingDirection);
        } else {
            sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        }
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<BuyerTableDto> buyerPage = buyerRepository.findAllByDeletedFalse(pageable).map(Buyer::build);
        log.info("success");
        return buyerPage;
    }

    @Override
    public Buyer save(
            @NotNull Buyer buyer
    ) throws StripeException {
        log.info("save buyer: {}", buyer);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String password = generatePassword();
        buyer.setPassword(bCryptPasswordEncoder.encode(password));
        sendPasswordEmail(password, buyer.getEmail());
        buyer.setPermission(permissionRepository.findByName("API"));

//        log.info("save customer in stripe");
//        Stripe.apiKey = stripeKey;
//        CustomerCreateParams customerCreateParams =
//                CustomerCreateParams.builder()
//                        .setName(buyer.getSurname() + " " + buyer.getName())
//                        .setEmail(buyer.getEmail())
//                        .build();
//
//        Customer customer = Customer.create(customerCreateParams);
//
//        log.info("success save customer in stripe");

        Buyer buyerAfterSave = buyerRepository.save(buyer);
        log.info("success save buyer");
        return buyerAfterSave;
    }

    @Override
    public Buyer update(
            @NotNull Buyer objectForm
    ) {
        log.info("update buyer: {}", objectForm);
        Buyer object = buyerRepository.findById(objectForm.getId()).orElseThrow();
        object.setName(objectForm.getName());
        object.setSurname(objectForm.getSurname());
        object.setLastname(objectForm.getLastname());
        object.setAddress(objectForm.getAddress());
        object.setIdNumber(objectForm.getIdNumber());
        object.setPassportSeries(objectForm.getPassportSeries());
        object.setPassportNumber(objectForm.getPassportNumber());
        object.setPassportWhoIssued(objectForm.getPassportWhoIssued());
        object.setPhone(objectForm.getPhone());
        object.setEmail(objectForm.getEmail());
        object.setNote(objectForm.getNote());
        object.setRealtor(objectForm.getRealtor());
        buyerRepository.save(object);
        log.info("success");
        return object;
    }

    @Override
    public void deleteById(
            @NotNull Long buyerId
    ) {
        log.info("set buyer.delete true by id: {}", buyerId);
        buyerRepository.setDeleted(buyerId);
        log.info("success set buyer.delete true");
    }

    @Override
    public List<Buyer> findByName(
            @NotNull String name
    ) {
        log.info("get buyers by name: {}", name);
        List<Buyer> buyers = null;
        if (name.contains(" ") && name.split(" ").length > 1) {
            String[] words = name.split(" ");
            if (words.length == 2) {
                buyers = buyerRepository.findByNameAndDeletedFalse(words[0], words[1]);
            }
        } else {
            name = name.replace(" ", "");
            buyers = buyerRepository.findByNameAndDeletedFalse(name);
        }
        log.info("success");
        return buyers;
    }

    @Override
    public Buyer findById(
            @NotNull Long id
    ) {
        log.info("get buyer by id: {}", id);
        Buyer object = buyerRepository.findById(id).orElseThrow();
        log.info("success");
        return object;
    }

    public void documentValidation(
            @NotNull BindingResult bindingResult,
            @NotNull BuyerSaveDto buyerSaveDto
    ) {
        if (buyerSaveDto.getDocumentStyle() == null || buyerSaveDto.getDocumentStyle().equals("")) {
            return;
        }
        DocumentStyle documentStyle = null;
        try {
            documentStyle = DocumentStyle.valueOf(buyerSaveDto.getDocumentStyle());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        if (documentStyle != null) {
            if (documentStyle.equals(DocumentStyle.ID_CARD)) {
                if (buyerSaveDto.getIdCardNumber() == null) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "idCardNumber", "Must not be empty"));
                } else if (buyerSaveDto.getIdCardNumber() > 9999999999999L || buyerSaveDto.getIdCardNumber() <= 999999999999L) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "idCardNumber", "Must be entered 13 numbers"));
                }
                if (buyerSaveDto.getIdCardWhoIssued() == null) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "idCardWhoIssued", "Must not be empty"));
                } else if (buyerSaveDto.getIdCardWhoIssued() > 9999 || buyerSaveDto.getIdCardWhoIssued() <= 999) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "idCardWhoIssued", "Must be entered 4 numbers"));
                }
            } else if (documentStyle.equals(DocumentStyle.PASSPORT)) {
                if (buyerSaveDto.getPassportSeries() == null || buyerSaveDto.getPassportSeries().equals("")) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "passportSeries", "Must not be empty"));
                } else if (buyerSaveDto.getPassportSeries().length() != 2) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "passportSeries", "Must be entered 2 letters"));
                }
                if (buyerSaveDto.getPassportNumber() == null) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "passportNumber", "Must not be empty"));
                } else if (buyerSaveDto.getPassportNumber() > 999999 || buyerSaveDto.getPassportNumber() <= 99999) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "passportNumber", "Must be entered 6 numbers"));
                }
                if (buyerSaveDto.getPassportWhoIssued() == null || buyerSaveDto.getPassportWhoIssued().equals("")) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "passportWhoIssued", "Must not be empty"));
                }
            }
        }
    }

    @Override
    public String generatePassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return gen.generatePassword(10,
                splCharRule,
                lowerCaseRule,
                upperCaseRule,
                digitRule);
    }

    @Override
    public void sendPasswordEmail(
            @NotNull String password,
            @NotNull String email
    ) {
        System.out.println(password);
        EmailContextBuyerPassword emailContext = new EmailContextBuyerPassword();
        emailContext.setPassword(password);
        emailContext.setTemplateLocation("email/email-registration");
        emailContext.setSubject("Your password");
        emailContext.setFrom("no-reply@javadevjournal.com");
        emailContext.setTo(email);
        new MailThread(mailService, emailContext).start();
    }

    @Override
    public void phoneValidation(
            @NotNull BindingResult bindingResult,
            @NotNull String phone) {
        if (phone.contains("_")) {
            bindingResult.addError(new FieldError("buyerSaveDto", "phone", "Phone must be valid"));
        }
    }

    @Override
    public void emailValidation(
            @NotNull BindingResult bindingResult,
            @NotNull String email,
            Long id
    ) {
        Buyer buyer;
        if(id != null){
            buyer = buyerRepository.findByEmail(email, id);
        } else {
            buyer = buyerRepository.findByEmail(email);
        }
        if(buyer != null){
            bindingResult.addError(new FieldError("buyerSaveDto", "email", "This email is already registered"));
        }
    }
}
