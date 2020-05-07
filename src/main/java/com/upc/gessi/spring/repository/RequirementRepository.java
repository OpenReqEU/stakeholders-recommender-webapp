package com.upc.gessi.spring.repository;

import com.upc.gessi.spring.entity.persistence.Requirement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequirementRepository extends CrudRepository<Requirement, String> {

    @Query("select r from Requirement r where LOWER(r.description) LIKE %?1%")
    List<Requirement> findByDescription(String description);

    @Query("select r from Requirement r where LOWER(r.description) LIKE %?1%" +
            " and (?2 is null or ?2 like CONCAT('%',r.product,'%') )" +
            " and (?3 is null or ?3 like CONCAT('%',r.component,'%') )" +
            " and (?4 is null or ?4 like CONCAT('%',LOWER(r.status),'%') )" +
            " and (?5 <= r.modified_at)")
    List<Requirement> filter(String text,
                             String products,
                             String components,
                             String statuses,
                             String date);
}
