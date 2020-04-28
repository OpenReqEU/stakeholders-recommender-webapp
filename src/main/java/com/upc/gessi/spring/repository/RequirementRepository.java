package com.upc.gessi.spring.repository;

import com.upc.gessi.spring.entity.persistence.Requirement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequirementRepository extends CrudRepository<Requirement, String> {

    @Query("select r from Requirement r where LOWER(r.description) LIKE %?1%")
    List<Requirement> findByDescription(String description);
}
