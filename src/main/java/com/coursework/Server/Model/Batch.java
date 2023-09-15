package com.coursework.Server.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long batchId;
    @Size(min=5, message = "Минимальная длина значения - 5 символов")
    private String type;
    @Min(value = 0, message = "Не должно быть меньше нуля")
    private int amount;
    @Min(value = 0, message = "Не должно быть меньше нуля")
    private float weight;
}
