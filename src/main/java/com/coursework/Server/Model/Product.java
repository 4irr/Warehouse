package com.coursework.Server.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    @ManyToOne
    private Batch batch;
    private String name, cell;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Float.compare(product.price, price) == 0 && productId.equals(product.productId) && batch.equals(product.batch) && name.equals(product.name) && cell.equals(product.cell) && shelfLife.equals(product.shelfLife);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, batch, name, cell, price, shelfLife);
    }
    @Min(value = 0, message = "Не должно быть меньше нуля")
    private float price;
    private LocalDate shelfLife;
}
