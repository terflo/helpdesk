package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Decision;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.dto.DecisionDTO;
import com.terflo.helpdesk.model.exceptions.DecisionNameAlreadyExistsException;
import com.terflo.helpdesk.model.exceptions.DecisionNotFoundException;
import com.terflo.helpdesk.model.repositories.DecisionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class DecisionService {

    private final DecisionRepository decisionRepository;

    public List<Decision> findAllDecisions() {
        return StreamSupport
                .stream(decisionRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public void deleteAllDecisionByAuthor(User author) {
        decisionRepository.deleteAllByAuthor(author);
    }

    public void deleteDecision(Long id) throws DecisionNotFoundException {
        if(!decisionRepository.findById(id).isPresent())
            throw new DecisionNotFoundException(
                    String.format("Частый вопрос с индификатором %s не найден", id));
        else
            decisionRepository.deleteById(id);
    }

    public Decision saveDecision(Decision decision) throws DecisionNameAlreadyExistsException {
        if(decisionRepository.findDecisionByName(decision.getName()).isPresent()) {
            throw new DecisionNameAlreadyExistsException(
                    String.format("Название %s частого вопроса уже существует", decision.getName())
            );
        }
        else
            return decisionRepository.save(decision);
    }

    public void updateDecision(DecisionDTO decisionDTO) throws DecisionNotFoundException {
        Decision decision = decisionRepository
                .findById(decisionDTO.id)
                .orElseThrow(() -> new DecisionNotFoundException(
                        String.format("Уникальный индификатор %s частого вопроса не был найден)", decisionDTO.id)
                ));
        decision.setName(decisionDTO.name);
        decision.setAnswer(decisionDTO.answer);
        decisionRepository.save(decision);
    }
}
