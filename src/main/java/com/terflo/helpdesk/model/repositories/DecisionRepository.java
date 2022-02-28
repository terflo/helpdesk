package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.Decision;
import com.terflo.helpdesk.model.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DecisionRepository extends CrudRepository<Decision, Long> {

    Optional<Decision> findDecisionByName(String name);

    void deleteAllByAuthor(User author);

}
