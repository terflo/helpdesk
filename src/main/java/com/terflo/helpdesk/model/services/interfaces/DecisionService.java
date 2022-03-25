package com.terflo.helpdesk.model.services.interfaces;

import com.terflo.helpdesk.model.entity.Decision;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.DecisionNameAlreadyExistsException;
import com.terflo.helpdesk.model.exceptions.DecisionNotFoundException;

import java.util.List;

public interface DecisionService {

    List<Decision> findAllDecisions();

    Decision saveDecision(Decision decision) throws DecisionNameAlreadyExistsException;

    Decision updateDecision(Decision newDecision) throws DecisionNotFoundException;

    void deleteAllDecisionByAuthor(User user);

    void deleteDecisionByID(Long id) throws DecisionNotFoundException;

}
