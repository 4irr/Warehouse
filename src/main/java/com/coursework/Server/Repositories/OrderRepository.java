package com.coursework.Server.Repositories;

import com.coursework.Server.Model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
