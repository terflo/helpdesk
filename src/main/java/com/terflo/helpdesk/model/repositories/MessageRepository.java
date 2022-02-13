package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {

    List<Message> findMessagesBySender(User sender);

    List<Message> findMessageByDate(Date date);

    List<Message> findAllByUserRequestOrderByDate(UserRequest userRequest);

}
