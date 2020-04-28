package com.upc.gessi.spring.repository;

import com.upc.gessi.spring.entity.persistence.Participant;
import org.springframework.data.repository.CrudRepository;

public interface ParticipantRepository extends CrudRepository<Participant, String> {
}
