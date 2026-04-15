package com.knmiet.et.controller;


import com.knmiet.et.data.Transaction;
import com.knmiet.et.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping("/transactions")
    public String transactions(Model model) {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String username = auth.getName();


        model.addAttribute("transactions", service.getByUser(username));
        return "transactions";
    }

    @GetMapping("/add-transaction")
    public String addForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "add-transaction";
    }

    @PostMapping("/add-transaction")
    public String save(Transaction transaction) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        transaction.setUsername(auth.getName());
        transaction.setDateTime(LocalDateTime.now());

        service.save(transaction);

        return "redirect:/transactions";
    }
}
