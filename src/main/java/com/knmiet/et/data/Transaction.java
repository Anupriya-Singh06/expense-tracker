package com.knmiet.et.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class Transaction {
    private String username;
    private String category;
    private LocalDateTime dateTime;
    private double amount;
}
