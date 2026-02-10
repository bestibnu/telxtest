package com.telxtest.service;

import com.telxtest.model.CallEventRequest;
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
import org.springframework.beans.factory.annotation.Value;
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
    private final BigDecimal ratePerMinute;

    public StoreService(
            ContactRepository contactRepository,
            CallRecordRepository callRecordRepository,
            CreditBalanceRepository creditBalanceRepository,
            @Value("${telx.call.rate-per-minute:0.05}") BigDecimal ratePerMinute
    ) {
        this.contactRepository = contactRepository;
        this.callRecordRepository = callRecordRepository;
        this.creditBalanceRepository = creditBalanceRepository;
        this.ratePerMinute = ratePerMinute;
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
    public CallRecord startCall(String from, String to) {
        CallRecordEntity entity = new CallRecordEntity();
        entity.setFromNumber(from);
        entity.setToNumber(to);
        entity.setStartedAt(Instant.now());
        entity.setStatus("initiated");
        entity.setProvider("app");
        return toCallRecord(callRecordRepository.save(entity));
    }

    @Transactional
    public CallRecord recordCallEvent(CallEventRequest event) {
        CallRecordEntity entity = callRecordRepository.findByCallSid(event.callSid())
                .orElseGet(CallRecordEntity::new);
        Instant eventTime = event.timestamp() != null ? event.timestamp() : Instant.now();

        if (entity.getStartedAt() == null) {
            entity.setStartedAt(eventTime);
        }
        entity.setFromNumber(event.from());
        entity.setToNumber(event.to());
        entity.setProvider(event.provider());
        entity.setCallSid(event.callSid());
        entity.setStatus(event.status());

        if (event.durationSec() != null) {
            entity.setDurationSec(event.durationSec());
        }

        if ("completed".equalsIgnoreCase(event.status())) {
            if (entity.getEndedAt() == null) {
                entity.setEndedAt(eventTime);
            }
            if (entity.getCost() == null) {
                BigDecimal cost = calculateCost(entity.getDurationSec());
                entity.setCost(cost);
                applyCharge(cost);
            }
        }

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
                entity.getStatus(),
                entity.getProvider(),
                entity.getCallSid(),
                entity.getDurationSec(),
                entity.getEndedAt(),
                entity.getCost()
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

    private BigDecimal calculateCost(Integer durationSec) {
        if (durationSec == null || durationSec <= 0) {
            return BigDecimal.ZERO;
        }
        long minutes = (durationSec + 59L) / 60L;
        return ratePerMinute.multiply(BigDecimal.valueOf(minutes));
    }

    private void applyCharge(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            return;
        }
        CreditBalanceEntity entity = creditBalanceRepository.findById(CREDIT_BALANCE_ID)
                .orElseGet(this::createDefaultBalance);
        entity.setBalance(entity.getBalance().subtract(amount));
        creditBalanceRepository.save(entity);
    }
}
