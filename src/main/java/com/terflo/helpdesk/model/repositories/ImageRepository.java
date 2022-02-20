package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ImageRepository extends CrudRepository<Image, Long> {

    @Override
    Optional<Image> findById(Long aLong);

    @Override
    <S extends Image> S save(S entity);
}
