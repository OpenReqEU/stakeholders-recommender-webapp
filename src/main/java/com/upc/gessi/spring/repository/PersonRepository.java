package com.upc.gessi.spring.repository;

import com.upc.gessi.spring.entity.persistence.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, String> {
}
