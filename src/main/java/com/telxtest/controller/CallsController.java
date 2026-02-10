package com.telxtest.controller;

import com.telxtest.model.CallEventRequest;
import com.telxtest.model.CallRecord;
import com.telxtest.model.CallRequest;
import com.telxtest.service.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calls")
public class CallsController {
    private final StoreService store;

    public CallsController(StoreService store) {
        this.store = store;
    }

    @GetMapping
    public List<CallRecord> listCalls() {
        return store.listCalls();
    }

    @PostMapping
    public CallRecord placeCall(@Valid @RequestBody CallRequest request) {
        return store.startCall(request.from(), request.to());
    }

    @PostMapping("/events")
    public CallRecord recordEvent(@Valid @RequestBody CallEventRequest request) {
        return store.recordCallEvent(request);
    }
}
