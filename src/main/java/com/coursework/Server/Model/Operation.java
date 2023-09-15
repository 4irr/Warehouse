package com.coursework.Server.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long operationId;
    @ManyToOne
    private User user;
    private LocalDate date;
    private String type;
}
