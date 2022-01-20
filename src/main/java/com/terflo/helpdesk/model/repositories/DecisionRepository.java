package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.Decision;
import org.springframework.data.repository.CrudRepository;

public interface DecisionRepository extends CrudRepository<Decision, Long> {
}
