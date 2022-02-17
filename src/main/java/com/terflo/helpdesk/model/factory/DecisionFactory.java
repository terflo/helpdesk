package com.terflo.helpdesk.model.factory;

import com.terflo.helpdesk.model.entity.Decision;
import com.terflo.helpdesk.model.entity.dto.DecisionDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DecisionFactory {

    private final UserFactory userFactory;

    public DecisionFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    public DecisionDTO convertToDecisionDTO(Decision decision) {
        return new DecisionDTO(
                decision.getId(),
                decision.getName(),
                decision.getAnswer(),
                decision.getDate(),
                userFactory.convertToUserDTO(decision.getAuthor())
        );
    }

    public List<DecisionDTO> convertToDecisionDTO(List<Decision> decisions) {
        return decisions
                .stream()
                .map(this::convertToDecisionDTO)
                .collect(Collectors.toList());
    }

}
