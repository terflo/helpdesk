package com.terflo.helpdesk.model.factories;

import com.terflo.helpdesk.model.entity.Decision;
import com.terflo.helpdesk.model.entity.dto.DecisionDTO;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DecisionFactory {

    private final UserFactory userFactory;

    private final UserService userService;

    public DecisionDTO convertToDecisionDTO(Decision decision) {
        return new DecisionDTO(
                decision.getId(),
                decision.getName(),
                decision.getAnswer(),
                decision.getDate(),
                userFactory.convertToUserDTO(decision.getAuthor())
        );
    }

    public Decision convertToDecision(DecisionDTO decisionDTO) throws UserNotFoundException {
        return new Decision(
                decisionDTO.id,
                decisionDTO.name,
                decisionDTO.answer,
                decisionDTO.date,
                decisionDTO.author == null ? null : userService.findUserByUsername(decisionDTO.author.username)
        );
    }

    public List<DecisionDTO> convertToDecisionDTO(List<Decision> decisions) {
        return decisions
                .stream()
                .map(this::convertToDecisionDTO)
                .collect(Collectors.toList());
    }

}
