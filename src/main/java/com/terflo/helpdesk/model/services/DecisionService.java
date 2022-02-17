package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Decision;
import com.terflo.helpdesk.model.entity.dto.DecisionDTO;
import com.terflo.helpdesk.model.exceptions.DecisionNameAlreadyExistsException;
import com.terflo.helpdesk.model.exceptions.DecisionNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.repositories.DecisionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DecisionService {

    private final DecisionRepository decisionRepository;

    public DecisionService(DecisionRepository decisionRepository) {
        this.decisionRepository = decisionRepository;
    }

    public List<Decision> findAllDecisions() {
        return StreamSupport
                .stream(decisionRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public void deleteDecision(Long id) throws DecisionNotFoundException {
        if(decisionRepository.findById(id).isPresent())
            decisionRepository.deleteById(id);
        else
            throw new DecisionNotFoundException("Частый вопрос с индификатором " + id.toString() + " не найден");
    }

    public void deleteDecision(Decision decision) throws DecisionNotFoundException {
        if(decisionRepository.findById(decision.getId()).isPresent())
            decisionRepository.delete(decision);
        else
            throw new DecisionNotFoundException("Частый вопрос с индификатором " + decision.getId().toString() + " не найден");
    }

    public void saveDecision(Decision decision) throws DecisionNameAlreadyExistsException {
        if(decisionRepository.findDecisionByName(decision.getName()).isPresent()) {
            throw new DecisionNameAlreadyExistsException("Такое название частого вопроса уже существует");
        }
        else
            decisionRepository.save(decision);
    }

    public void updateDecision(Decision decision) throws DecisionNotFoundException {
        if(!decisionRepository.findById(decision.getId()).isPresent())
            throw new DecisionNotFoundException("Уникальный индификатор" + decision.getId() + " частого вопроса не был найден");
        else
            decisionRepository.save(decision);
    }

    public void updateDecision(DecisionDTO decisionDTO) throws DecisionNotFoundException {
        Decision decision = decisionRepository.findById(decisionDTO.id).orElseThrow(() -> new DecisionNotFoundException("Уникальный индификатор" + decisionDTO.id + " частого вопроса не был найден"));
        decision.setName(decisionDTO.name);
        decision.setAnswer(decisionDTO.answer);
        decisionRepository.save(decision);
    }
}
