package com.knmiet.et.controller;


import com.knmiet.et.data.Transaction;
import com.knmiet.et.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.stream.*;


import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService service;


    @GetMapping("/transactions")
    public String transactions(Model model) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<Transaction> list = service.getByUser(username);

        // sort latest first
        list = list.stream()
                .sorted(Comparator.comparing(Transaction::getDateTime).reversed())
                .toList();

        // group by month-year
        Map<String, List<Transaction>> groupedTransactions =
                list.stream()
                        .collect(Collectors.groupingBy(
                                t -> t.getDateTime().getMonth() + " " + t.getDateTime().getYear(),
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));

        // monthly totals
        Map<String, Double> monthlyTotals = new HashMap<>();

        groupedTransactions.forEach((month, txList) -> {
            double total = txList.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            monthlyTotals.put(month, total);
        });

        model.addAttribute("groupedTransactions", groupedTransactions);
        model.addAttribute("monthlyTotals", monthlyTotals);

        return "transactions";
    }

    @GetMapping("/add-transaction")
    public String addForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "add-transaction";
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<Transaction> list = service.getByUser(username);

        // 🔽 Sort latest first
        list = list.stream()
                .sorted(Comparator.comparing(Transaction::getDateTime).reversed())
                .toList();

        // =========================
        // 💰 Overall Total
        // =========================
        double totalAmount = list.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        // =========================
        // 📅 Group by Month-Year
        // =========================
        Map<String, List<Transaction>> groupedTransactions =
                list.stream()
                        .collect(Collectors.groupingBy(
                                t -> t.getDateTime().getMonth() + " " + t.getDateTime().getYear(),
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));

        // =========================
        // 💰 Monthly Totals
        // =========================
        Map<String, Double> monthlyTotals = new HashMap<>();

        groupedTransactions.forEach((month, txList) -> {
            double sum = txList.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            monthlyTotals.put(month, sum);
        });

        // =========================
        // 🥧 Current Month Pie Chart
        // =========================
        LocalDate now = LocalDate.now();

        Map<String, Double> currentMonthData =
                list.stream()
                        .filter(t -> t.getDateTime().getMonth() == now.getMonth()
                                && t.getDateTime().getYear() == now.getYear())
                        .collect(Collectors.groupingBy(
                                Transaction::getCategory,
                                Collectors.summingDouble(Transaction::getAmount)
                        ));

        // =========================
        // 📈 Last 3 Months Line Chart
        // =========================
        LocalDate threeMonthsAgo = now.minusMonths(2);

        Map<String, Map<String, Double>> lineData =
                list.stream()
                        .filter(t -> !t.getDateTime().toLocalDate().isBefore(threeMonthsAgo))
                        .collect(Collectors.groupingBy(
                                t -> t.getDateTime().getMonth().toString(),
                                LinkedHashMap::new,
                                Collectors.groupingBy(
                                        Transaction::getCategory,
                                        Collectors.summingDouble(Transaction::getAmount)
                                )
                        ));

        // =========================
        // 📤 Send to UI
        // =========================
        model.addAttribute("groupedTransactions", groupedTransactions);
        model.addAttribute("monthlyTotals", monthlyTotals);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("currentMonthData", currentMonthData);
        model.addAttribute("lineData", lineData);

        return "dashboard";
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
