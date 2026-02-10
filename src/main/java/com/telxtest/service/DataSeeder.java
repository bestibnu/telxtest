package com.telxtest.service;

import com.telxtest.persistence.entity.CallRecordEntity;
import com.telxtest.persistence.entity.ContactEntity;
import com.telxtest.persistence.repository.CallRecordRepository;
import com.telxtest.persistence.repository.ContactRepository;
import java.time.Instant;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {
    private final ContactRepository contactRepository;
    private final CallRecordRepository callRecordRepository;

    public DataSeeder(ContactRepository contactRepository, CallRecordRepository callRecordRepository) {
        this.contactRepository = contactRepository;
        this.callRecordRepository = callRecordRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (contactRepository.count() == 0) {
            ContactEntity alex = new ContactEntity();
            alex.setName("Alex Doe");
            alex.setPhone("+12025550101");
            contactRepository.save(alex);

            ContactEntity sam = new ContactEntity();
            sam.setName("Sam Kim");
            sam.setPhone("+442071838750");
            contactRepository.save(sam);
        }

        if (callRecordRepository.count() == 0) {
            CallRecordEntity seedCall = new CallRecordEntity();
            seedCall.setFromNumber("+12025550101");
            seedCall.setToNumber("+442071838750");
            seedCall.setStartedAt(Instant.now());
            seedCall.setStatus("completed");
            callRecordRepository.save(seedCall);
        }
    }
}
