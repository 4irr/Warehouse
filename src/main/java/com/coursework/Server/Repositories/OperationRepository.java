package com.coursework.Server.Repositories;

import com.coursework.Server.Model.Operation;
import org.springframework.data.repository.CrudRepository;

public interface OperationRepository extends CrudRepository<Operation, Long> {
}
