package com.knmiet.et.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knmiet.et.data.Transaction;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class TransactionService {

    private static final String FILE_PATH = "transactions.json";

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);;

    public List<Transaction> getAll() {
        try {
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
                objectMapper.writeValue(file, new ArrayList<>());
            }

            return objectMapper.readValue(
                    file,
                    new TypeReference<List<Transaction>>() {}
            );

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void save(Transaction transaction) {
        try {
            List<Transaction> list = getAll();
            list.add(transaction);

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getByUser(String username) {
        return getAll().stream()
                .filter(t -> t.getUsername().equals(username))
                .toList();
    }
}
