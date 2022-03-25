package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Image;
import com.terflo.helpdesk.model.exceptions.ImageNotFoundException;
import com.terflo.helpdesk.model.repositories.ImageRepository;
import com.terflo.helpdesk.model.services.interfaces.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Override
    public Image getImageByID(Long id) throws ImageNotFoundException {
        if(id == null) throw new ImageNotFoundException("Изображение не найдено");
        return imageRepository
                .findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Изображение не найдено"));
    }

    @Override
    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    @Override
    @Transactional
    public Image updateImageByID(Long id, Image newImage) throws ImageNotFoundException {
        Image image = imageRepository
                .findById(id)
                .orElseThrow(() -> new ImageNotFoundException(String.format("Изображение #%s не найдено", id)));
        image.setBytes(newImage.getBytes());
        image.setType(newImage.getType());
        return imageRepository.save(image);
    }

    @Override
    @Transactional
    public void deleteImageByID(Long id) throws ImageNotFoundException {
        if(id == null)
            return;
        if(!imageRepository.findById(id).isPresent())
            throw new ImageNotFoundException("Изображение не найдено");
        else
            imageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteImage(Image image) throws ImageNotFoundException {
        if(image == null)
            return;
        if(!imageRepository.findById(image.getId()).isPresent())
            throw new ImageNotFoundException("Изображение не найдено");
        else
            imageRepository.delete(image);
    }
}
