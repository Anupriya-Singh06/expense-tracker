package com.knmiet.et.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knmiet.et.data.AppUser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserFileService {

    private static final String FILE_PATH = "users.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<AppUser> getAllUsers() {
        try {
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
                objectMapper.writeValue(file, new ArrayList<>());
            }

            return objectMapper.readValue(
                    file,
                    new TypeReference<List<AppUser>>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveUser(AppUser user) {
        try {
            System.out.println("Save User File Service ");
            List<AppUser> users = getAllUsers();
            users.add(user);

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), users);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<AppUser> findByUsername(String username) {
        return getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username)
                || u.getEmail().equals(username))
                .findFirst();
    }
}