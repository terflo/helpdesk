package com.terflo.helpdesk.model.services.interfaces;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.exceptions.MessageNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;

import java.util.List;

public interface MessageService {

    Long countNewMessagesByUserRequestID(Long userRequestID) throws UserRequestNotFoundException;

    Message findMessageByID(Long id) throws MessageNotFoundException;

    List<Message> findMessagesByUserRequest (UserRequest userRequest);

    Message saveMessage(Message message);



}
