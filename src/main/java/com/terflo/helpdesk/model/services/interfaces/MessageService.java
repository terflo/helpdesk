package com.terflo.helpdesk.model.services.interfaces;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.exceptions.MessageNotFoundException;

import java.util.List;

public interface MessageService {

    Long countNewMessagesByUserRequest(UserRequest userRequest);

    Message findMessageByID(Long id) throws MessageNotFoundException;

    List<Message> findMessagesByUserRequest (UserRequest userRequest);

    Message saveMessage(Message message);



}
