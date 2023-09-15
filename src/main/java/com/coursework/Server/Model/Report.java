package com.coursework.Server.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reportId;
    @ManyToOne
    private User user;
    private LocalDate beginning, ending;
    private float turnover, expenses, productivity, turnoverRatio;

    public Report(User user, LocalDate beginning, LocalDate ending, Float turnover, Float expenses, Float productivity, Float turnoverRatio){
        this.user = user;
        this.beginning = beginning;
        this.ending = ending;
        this.turnover = turnover;
        this.expenses =expenses;
        this.productivity = productivity;
        this.turnoverRatio = turnoverRatio;
    }
}
