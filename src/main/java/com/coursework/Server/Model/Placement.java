package com.coursework.Server.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Placement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long placementId;
    @OneToOne
    private Operation operation;
    @OneToOne
    private Batch batch;
}
