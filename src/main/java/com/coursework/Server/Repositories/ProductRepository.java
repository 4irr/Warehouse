package com.coursework.Server.Repositories;

import com.coursework.Server.Model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
