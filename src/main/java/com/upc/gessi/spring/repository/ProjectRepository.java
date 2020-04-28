package com.upc.gessi.spring.repository;

import com.upc.gessi.spring.entity.persistence.Project;
import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<Project, String> {
}
