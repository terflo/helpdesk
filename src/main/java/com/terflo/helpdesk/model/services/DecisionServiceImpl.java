package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Decision;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.DecisionNameAlreadyExistsException;
import com.terflo.helpdesk.model.exceptions.DecisionNotFoundException;
import com.terflo.helpdesk.model.repositories.DecisionRepository;
import com.terflo.helpdesk.model.services.interfaces.DecisionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class DecisionServiceImpl implements DecisionService {

    /**
     * Репозиторий с часто задаваемыми вопросами
     */
    private final DecisionRepository decisionRepository;

    @Override
    public List<Decision> findAllDecisions() {
        return StreamSupport
                .stream(decisionRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllDecisionByAuthor(User author) {
        decisionRepository.deleteAllByAuthor(author);
    }

    @Override
    public void deleteDecisionByID(Long id) throws DecisionNotFoundException {
        if(!decisionRepository.findById(id).isPresent())
            throw new DecisionNotFoundException(
                    String.format("Частый вопрос с индификатором %s не найден", id));
        else
            decisionRepository.deleteById(id);
    }

    @Override
    public Decision saveDecision(Decision decision) throws DecisionNameAlreadyExistsException {

        if(decisionRepository.findDecisionByName(decision.getName()).isPresent()) {
            throw new DecisionNameAlreadyExistsException(
                    String.format("Название %s частого вопроса уже существует", decision.getName()));
        } else return decisionRepository.save(decision);
    }

    @Override
    public Decision updateDecision(Decision newDecision) throws DecisionNotFoundException {
        Decision decision = decisionRepository
                .findById(newDecision.getId())
                .orElseThrow(() -> new DecisionNotFoundException(
                        String.format("Уникальный индификатор %s частого вопроса не был найден)", newDecision.getId())));
        decision.setName(decision.getName());
        decision.setAnswer(decision.getAnswer());
        return decisionRepository.save(decision);
    }
}
