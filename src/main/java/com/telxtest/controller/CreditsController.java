package com.telxtest.controller;

import com.telxtest.model.CreditBalance;
import com.telxtest.model.TopUpRequest;
import com.telxtest.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credits")
public class CreditsController {
    private final StoreService store;

    public CreditsController(StoreService store) {
        this.store = store;
    }

    @GetMapping
    public CreditBalance getBalance() {
        return store.getBalance();
    }

    @PostMapping("/topup")
    public CreditBalance topUp(@Valid @RequestBody TopUpRequest request) {
        return store.topUp(request.amount());
    }
}
