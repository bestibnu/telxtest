package com.telxtest.service;

import com.telxtest.model.CallRecord;
import com.telxtest.model.Contact;
import com.telxtest.model.CreditBalance;
import com.telxtest.persistence.entity.CallRecordEntity;
import com.telxtest.persistence.entity.ContactEntity;
import com.telxtest.persistence.entity.CreditBalanceEntity;
import com.telxtest.persistence.repository.CallRecordRepository;
import com.telxtest.persistence.repository.ContactRepository;
import com.telxtest.persistence.repository.CreditBalanceRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreService {
    private static final long CREDIT_BALANCE_ID = 1L;
    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("10.00");
    private static final String DEFAULT_CURRENCY = "USD";

    private final ContactRepository contactRepository;
    private final CallRecordRepository callRecordRepository;
    private final CreditBalanceRepository creditBalanceRepository;

    public StoreService(
            ContactRepository contactRepository,
            CallRecordRepository callRecordRepository,
            CreditBalanceRepository creditBalanceRepository
    ) {
        this.contactRepository = contactRepository;
        this.callRecordRepository = callRecordRepository;
        this.creditBalanceRepository = creditBalanceRepository;
    }

    @Transactional(readOnly = true)
    public List<Contact> listContacts() {
        return contactRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(this::toContact)
                .toList();
    }

    @Transactional
    public Contact addContact(Contact contact) {
        ContactEntity entity = new ContactEntity();
        entity.setName(contact.name());
        entity.setPhone(contact.phone());
        return toContact(contactRepository.save(entity));
    }

    @Transactional
    public boolean deleteContact(String id) {
        Optional<Long> parsedId = parseId(id);
        if (parsedId.isEmpty() || !contactRepository.existsById(parsedId.get())) {
            return false;
        }
        contactRepository.deleteById(parsedId.get());
        return true;
    }

    @Transactional(readOnly = true)
    public List<CallRecord> listCalls() {
        return callRecordRepository.findAllByOrderByStartedAtDesc().stream()
                .map(this::toCallRecord)
                .toList();
    }

    @Transactional
    public CallRecord recordCall(String from, String to, String status) {
        CallRecordEntity entity = new CallRecordEntity();
        entity.setFromNumber(from);
        entity.setToNumber(to);
        entity.setStartedAt(Instant.now());
        entity.setStatus(status);
        return toCallRecord(callRecordRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public CreditBalance getBalance() {
        CreditBalanceEntity entity = creditBalanceRepository.findById(CREDIT_BALANCE_ID)
                .orElseGet(this::createDefaultBalance);
        return toCreditBalance(entity);
    }

    @Transactional
    public CreditBalance topUp(BigDecimal amount) {
        CreditBalanceEntity entity = creditBalanceRepository.findById(CREDIT_BALANCE_ID)
                .orElseGet(this::createDefaultBalance);
        entity.setBalance(entity.getBalance().add(amount));
        return toCreditBalance(creditBalanceRepository.save(entity));
    }

    private Contact toContact(ContactEntity entity) {
        return new Contact(String.valueOf(entity.getId()), entity.getName(), entity.getPhone());
    }

    private CallRecord toCallRecord(CallRecordEntity entity) {
        return new CallRecord(
                String.valueOf(entity.getId()),
                entity.getFromNumber(),
                entity.getToNumber(),
                entity.getStartedAt(),
                entity.getStatus()
        );
    }

    private CreditBalance toCreditBalance(CreditBalanceEntity entity) {
        return new CreditBalance(entity.getBalance(), entity.getCurrency());
    }

    private Optional<Long> parseId(String id) {
        try {
            return Optional.of(Long.parseLong(id));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private CreditBalanceEntity createDefaultBalance() {
        CreditBalanceEntity entity = new CreditBalanceEntity();
        entity.setId(CREDIT_BALANCE_ID);
        entity.setBalance(DEFAULT_BALANCE);
        entity.setCurrency(DEFAULT_CURRENCY);
        return creditBalanceRepository.save(entity);
    }
}
