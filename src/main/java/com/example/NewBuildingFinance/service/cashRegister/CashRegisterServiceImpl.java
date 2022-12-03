package com.example.NewBuildingFinance.service.cashRegister;

import com.example.NewBuildingFinance.dto.cashRegister.CashRegisterTableDto;
import com.example.NewBuildingFinance.dto.cashRegister.CashRegisterTableDtoForFlat;
import com.example.NewBuildingFinance.dto.cashRegister.IncomeUploadDto;
import com.example.NewBuildingFinance.dto.cashRegister.SpendingUploadDto;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.cashRegister.Article;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.cashRegister.StatusCashRegister;
import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.others.mail.context.AbstractEmailContextBuyerFlatPayment;
import com.example.NewBuildingFinance.others.mail.MailThread;
import com.example.NewBuildingFinance.others.specifications.CashRegisterSpecification;
import com.example.NewBuildingFinance.repository.CashRegisterRepository;
import com.example.NewBuildingFinance.repository.FlatPaymentRepository;
import com.example.NewBuildingFinance.repository.RealtorRepository;
import com.example.NewBuildingFinance.repository.auth.UserRepository;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.mail.MailServiceImpl;
import com.example.NewBuildingFinance.service.setting.SettingServiceImpl;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdfparser.XrefTrailerResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
@AllArgsConstructor
public class CashRegisterServiceImpl implements CashRegisterService{
    private final CashRegisterRepository cashRegisterRepository;
    private final FlatPaymentRepository flatPaymentRepository;
    private final RealtorRepository realtorRepository;
    private final UserRepository userRepository;

    private final MailServiceImpl mailServiceImpl;
    private final SettingServiceImpl settingService;
    private final InternalCurrencyServiceImpl internalCurrencyService;

    @Override
    public Page<CashRegisterTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Long number,
            String dateStart,
            String dateFin,
            String economic,
            String status,
            Long objectId,
            String article,
            Double priceStart,
            Double priceFin,
            Long currencyId,
            String counterparty
    ) throws ParseException {
        log.info("get cash register page. page: {}, size: {} field: {}, direction: {}",
                currentPage - 1, size, sortingField, sortingDirection);
        Specification<CashRegister> specification = Specification
                .where(CashRegisterSpecification.likeNumber(number))
                .and(CashRegisterSpecification.likeDate(dateStart, dateFin))
                .and(CashRegisterSpecification.likeEconomic(economic))
                .and(CashRegisterSpecification.likeStatus(status))
                .and(CashRegisterSpecification.likeObjectId(objectId))
                .and(CashRegisterSpecification.likeArticle(article))
                .and(CashRegisterSpecification.likePrice(priceStart, priceFin))
                .and(CashRegisterSpecification.likeCurrencyId(currencyId))
                .and(CashRegisterSpecification.likeCounterparty(counterparty));
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<CashRegisterTableDto> cashRegisterPage = cashRegisterRepository.findAll(specification, pageable).map(CashRegister::build);
        log.info("success get cash register page");
        return cashRegisterPage;
    }

    @Override
    public List<CashRegisterTableDtoForFlat> getCashRegistersByFlatId(Long id) {
        List<CashRegister> cashRegisters = cashRegisterRepository.findAllByFlatId(id);
        return cashRegisters.stream().map(CashRegister::buildForFlat).collect(Collectors.toList());
    }

    @Override
    public CashRegister saveIncome(CashRegister income) {
        log.info("save income: {}", income);
        CashRegister cashRegister = cashRegisterRepository.save(income);
        if (income.getArticle().equals(Article.FLAT_PAYMENT)) {
            FlatPayment flatPayment = flatPaymentRepository.findById(cashRegister.getFlatPayment().getId()).orElse(null);
            if (flatPayment != null) {
                cashRegister.setCounterparty(
                        flatPayment.getFlat().getBuyer().getSurname() + " " +
                        flatPayment.getFlat().getBuyer().getName());
                flatPayment.setActually(cashRegister.getPrice() / internalCurrencyService.getUSD().getPrice() * cashRegister.getAdmissionCourse());
                flatPayment.setPaid(true);
                flatPaymentRepository.save(flatPayment);

                if(income.getStatus().equals(StatusCashRegister.COMPLETED)) {
                    AbstractEmailContextBuyerFlatPayment emailContext = new AbstractEmailContextBuyerFlatPayment();
                    emailContext.setTemplateLocation("email/email-buyer-notification"); // вказуєм з якої папки взяти html
                    emailContext.setSubject("Complete your registration");
                    emailContext.setFrom("no-reply@javadevjournal.com");
                    emailContext.setTo(flatPayment.getFlat().getBuyer().getEmail());
                    emailContext.setDate(flatPayment.getDate());
                    emailContext.setTodayDate(new Date());
                    emailContext.setPlanned(flatPayment.getPlanned());
                    emailContext.setActually(flatPayment.getActually());
                    new MailThread(mailServiceImpl, emailContext).start();
                }
            }
        }
        log.info("success save income");
        return cashRegister;
    }

    @Override
    public IncomeUploadDto updateIncome(CashRegister income) {
        log.info("update income: {}", income);
        CashRegister cashRegister = cashRegisterRepository.findById(income.getId()).orElseThrow();
        Article article = cashRegister.getArticle();
        FlatPayment oldFlatPayment = cashRegister.getFlatPayment();
        CashRegister cashRegisterAfterSave = cashRegisterRepository.save(income);
        if (income.getArticle().equals(Article.FLAT_PAYMENT)) {
            FlatPayment newFlatPayment = flatPaymentRepository.findById(cashRegisterAfterSave.getFlatPayment().getId()).orElse(null);
            if (newFlatPayment != null) {
                if(!oldFlatPayment.equals(newFlatPayment)){
                    oldFlatPayment.setActually(null);
                    oldFlatPayment.setPaid(false);
                    flatPaymentRepository.save(oldFlatPayment);
                }

                cashRegisterAfterSave.setCounterparty(
                        newFlatPayment.getFlat().getBuyer().getSurname() + " " +
                                newFlatPayment.getFlat().getBuyer().getName());
                newFlatPayment.setActually(cashRegisterAfterSave.getPrice() / internalCurrencyService.getUSD().getPrice() * cashRegisterAfterSave.getAdmissionCourse());
                newFlatPayment.setPaid(true);
                flatPaymentRepository.save(newFlatPayment);

                if(income.getStatus().equals(StatusCashRegister.COMPLETED)) {
                    if(settingService.getSettings().isSendEmailToBuyers()) {
                        AbstractEmailContextBuyerFlatPayment emailContext = new AbstractEmailContextBuyerFlatPayment();
                        emailContext.setTemplateLocation("email/email-buyer-notification"); // вказуєм з якої папки взяти html
                        emailContext.setSubject("Complete your registration");
                        emailContext.setFrom("no-reply@javadevjournal.com");
                        emailContext.setTo(newFlatPayment.getFlat().getBuyer().getEmail());
                        emailContext.setDate(newFlatPayment.getDate());
                        emailContext.setTodayDate(new Date());
                        emailContext.setPlanned(newFlatPayment.getPlanned());
                        emailContext.setActually(newFlatPayment.getActually());
                        new MailThread(mailServiceImpl, emailContext).start();
                    }
                }
            }
        }
        if (!article.equals(cashRegisterAfterSave.getArticle()) && cashRegisterAfterSave.getArticle().equals(Article.OTHER_PAYMENT)){
            if (oldFlatPayment != null) {
                oldFlatPayment.setActually(null);
                oldFlatPayment.setPaid(false);
                flatPaymentRepository.save(oldFlatPayment);
            }
        }
        log.info("success update income");
        return cashRegisterAfterSave.buildIncomeUploadDto();
    }

    @Override
    public CashRegister saveSpending(CashRegister spending) {
        log.info("save spending: {}", spending);
        if(spending.getArticle().equals(Article.COMMISSION_AGENCIES)){
            Realtor realtor = realtorRepository.findById(spending.getRealtor().getId()).orElseThrow();
            spending.setCounterparty(realtor.getSurname() + " " + realtor.getName());
        } else if(spending.getArticle().equals(Article.COMMISSION_MANAGER)){
            User user = userRepository.findById(spending.getManager().getId()).orElseThrow();
            spending.setCounterparty(user.getSurname() + " " + user.getName());
        } else if(spending.getArticle().equals(Article.MONEY_FOR_DIRECTOR)){
            User user = userRepository.findById(1L).orElseThrow();
            spending.setCounterparty(user.getSurname() + " " + user.getName());
        }
        CashRegister cashRegister = cashRegisterRepository.save(spending);
        log.info("success save spending");
        return cashRegister;
    }

    @Override
    public SpendingUploadDto updateSpending(CashRegister spending) {
        log.info("update spending: {}", spending);
        if(spending.getArticle().equals(Article.COMMISSION_AGENCIES)){
            Realtor realtor = realtorRepository.findById(spending.getRealtor().getId()).orElseThrow();
            spending.setCounterparty(realtor.getSurname() + " " + realtor.getName());
        } else if(spending.getArticle().equals(Article.COMMISSION_MANAGER)){
            User user = userRepository.findById(spending.getManager().getId()).orElseThrow();
            spending.setCounterparty(user.getSurname() + " " + user.getName());
        } else if(spending.getArticle().equals(Article.MONEY_FOR_DIRECTOR)){
            User user = userRepository.findById(1L).orElseThrow();
            spending.setCounterparty(user.getSurname() + " " + user.getName());
        }
        CashRegister cashRegister = cashRegisterRepository.save(spending);
        log.info("success update spending");
        return cashRegister.buildSpendingUploadDto();
    }

    @Override
    public void deleteSpendingById(Long cashRegisterId) {
        log.info("set cash_register.delete true for spending by id: {}", cashRegisterId);
        cashRegisterRepository.setDeleted(cashRegisterId);
        log.info("success set cash_register.delete true for spending");
    }

    @Override
    public void deleteIncomeById(Long cashRegisterId) {
        log.info("set cash_register.delete true for income by id: {}", cashRegisterId);
        CashRegister cashRegister = cashRegisterRepository.findById(cashRegisterId).orElseThrow();
        if (cashRegister.getArticle().equals(Article.FLAT_PAYMENT)){
            cashRegister.setFlatPayment(null);
            cashRegister.setObject(null);
            FlatPayment oldFlatPayment = cashRegister.getFlatPayment();
            if (oldFlatPayment != null) {
                oldFlatPayment.setActually(null);
                oldFlatPayment.setPaid(false);
                flatPaymentRepository.save(oldFlatPayment);
            }
        }
        cashRegister.setDeleted(true);
        cashRegisterRepository.save(cashRegister);
        log.info("success set cash_register.delete true for income");
    }

    @Override
    public IncomeUploadDto findIncomeById(Long id) {
        log.info("get income by id: {}", id);
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow();
        IncomeUploadDto cashRegisterIncomeUploadDto = cashRegister.buildIncomeUploadDto();
        log.info("success get income by id");
        return cashRegisterIncomeUploadDto;
    }

    @Override
    public SpendingUploadDto findSpendingById(Long id) {
        log.info("get spending by id: {}", id);
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow();
        SpendingUploadDto cashRegisterSpendingUploadDto = cashRegister.buildSpendingUploadDto();
        log.info("success get spending by id");
        return cashRegisterSpendingUploadDto;
    }

    @Override
    public CashRegister findById(Long id) {
        log.info("get cash register by id: {}", id);
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow();
        log.info("success get cash register by id");
        return cashRegister;
    }

    @Override
    public CashRegister findIncomeByFlatPaymentId(Long flatPaymentId) {
        log.info("get income by flat payment id: {}", flatPaymentId);
        CashRegister cashRegister = cashRegisterRepository.findByFlatPaymentId(flatPaymentId).orElse(null);
        log.info("success get income by flat payment id");
        return cashRegister;
    }

    @Override
    public CashRegister findSpendingByFlatId(Long flatId) {
        log.info("get spending by flat id: {}", flatId);
        CashRegister cashRegister = cashRegisterRepository.findByFlatId(flatId).orElse(null);
        log.info("success get spending by flat id");
        return cashRegister;
    }

    @Override
    public ResponseEntity<byte[]> getPdfIncome(Long id) throws IOException, DocumentException {
        log.info("get pdf for income by id: {}", id);
        CashRegister income = cashRegisterRepository.findById(id).orElseThrow();

        Document document = new Document();
        File pdf = new File("src/main/resources/income.pdf");
        PdfWriter.getInstance(document, new FileOutputStream(pdf));

        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, BaseColor.BLACK);

        Paragraph paragraphHead = new Paragraph(new Chunk("Income", font));
        paragraphHead.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphHead);
        document.add(new Chunk());

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Paragraph paragraphDate = new Paragraph(new Chunk("date:" + format.format(income.getDate()), font));
        paragraphDate.setAlignment(Element.ALIGN_RIGHT);
        document.add(paragraphDate);
        document.add(new Chunk());

        if (income.getArticle().equals(Article.FLAT_PAYMENT)){
            Paragraph paragraphSender = new Paragraph(new Chunk("Sender: " + income.getCounterparty(), font));
            paragraphSender.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraphSender);
            document.add(new Chunk());
        }

        Paragraph paragraphRecipient = new Paragraph(new Chunk("Recipient: New Building Finance", font));
        paragraphRecipient.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphRecipient);
        document.add(new Chunk());

        if (income.getArticle().equals(Article.FLAT_PAYMENT)){
            PdfPTable table = new PdfPTable(5);
            Stream.of("Number", "Name", "Payment number", "Price", "Money")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });
            table.addCell(income.getNumber().toString());
            table.addCell(income.getArticle().getValue());
            table.addCell(income.getFlatPayment().getNumber().toString());
            table.addCell(income.getFlatPayment().getPlanned().toString());
            table.addCell(income.getPrice().toString());
            document.add(table);
            document.add(new Chunk());
        } else if(income.getArticle().equals(Article.OTHER_PAYMENT)){
            PdfPTable table = new PdfPTable(3);
            Stream.of("Number", "Name", "Money")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(1);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });
            table.addCell(income.getNumber().toString());
            table.addCell(income.getArticle().getValue());
            table.addCell(income.getPrice().toString());
            document.add(table);
            document.add(new Chunk());
        }

        if (income.getComment() != null && !income.getComment().equals("")) {
            Paragraph paragraphComment = new Paragraph(new Chunk("Comment: " + income.getComment(), font));
            document.add(paragraphComment);
        }

        document.add(new Chunk());

        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(settingService.getSettings().getPdfFooter()));
        document.close();

        byte[] content = Files.readAllBytes(pdf.toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "income" + format.format(income.getDate()) + ".pdf",
                "income" + format.format(income.getDate()) + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        log.info("success get pdf for income");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdfSpending(Long id) throws IOException, DocumentException {
        log.info("get pdf for spending by id: {}", id);
        CashRegister spending = cashRegisterRepository.findById(id).orElseThrow();

        Document document = new Document();
        File pdf = new File("src/main/resources/spending.pdf");
        PdfWriter.getInstance(document, new FileOutputStream(pdf));

        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, BaseColor.BLACK);

        Paragraph paragraphHead = new Paragraph(new Chunk("Spending", font));
        paragraphHead.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphHead);
        document.add(new Chunk());

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Paragraph paragraphDate = new Paragraph(new Chunk("date:" + format.format(spending.getDate()), font));
        paragraphDate.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphDate);
        document.add(new Chunk());

        Paragraph paragraphSender = new Paragraph(new Chunk("Sender: " + spending.getCounterparty(), font));
        document.add(paragraphSender);
        document.add(new Chunk());

        Paragraph paragraphRecipient = new Paragraph(new Chunk("Recipient: New Building Finance", font));
        document.add(paragraphRecipient);
        document.add(new Chunk());

        PdfPTable table = new PdfPTable(3);
        Stream.of("Number", "Name", "Money")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
        table.addCell(spending.getNumber().toString());
        table.addCell(spending.getArticle().getValue());
        table.addCell(spending.getPrice().toString());
        document.add(table);
        document.add(new Chunk());

        if (spending.getComment() != null && !spending.getComment().equals("")) {
            Paragraph paragraphComment = new Paragraph(new Chunk("Comment: " + spending.getComment(), font));
            document.add(paragraphComment);
        }

        document.add(new Chunk());

        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(settingService.getSettings().getPdfFooter()));
        document.close();

        byte[] content = Files.readAllBytes(pdf.toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "income" + format.format(spending.getDate()) + ".pdf",
                "income" + format.format(spending.getDate()) + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        log.info("success get pdf for spending");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @Override
    public boolean checkNumber(Long number) {
        CashRegister cashRegister = cashRegisterRepository.findByNumber(number);
        return cashRegister != null;
    }

    @Override
    public boolean checkCashRegister(Long id) {
        CashRegister cashRegister = null;
        if (id != null) {
            cashRegister = cashRegisterRepository.findById(id).orElse(null);
        }
        return cashRegister == null;
    }
}
