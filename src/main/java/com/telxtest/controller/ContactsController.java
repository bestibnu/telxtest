package com.telxtest.controller;

import com.telxtest.model.Contact;
import com.telxtest.service.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contacts")
public class ContactsController {
    private final StoreService store;

    public ContactsController(StoreService store) {
        this.store = store;
    }

    @GetMapping
    public List<Contact> listContacts() {
        return store.listContacts();
    }

    @PostMapping
    public Contact addContact(@Valid @RequestBody Contact contact) {
        return store.addContact(contact);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {
        if (store.deleteContact(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
