package com.coursework.Server.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "releases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Release {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long releaseId;
    @OneToOne
    private Operation operation;
    @OneToOne
    private Order order;
}
