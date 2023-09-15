package com.coursework.Server.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Acceptance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long acceptanceId;
    @OneToOne
    private Operation operation;
    @OneToOne
    private Batch batch;
    private String sender;
    @Min(value = 0, message = "Не должно быть меньше нуля")
    private float price;
}
