package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Image;
import com.terflo.helpdesk.model.exceptions.ImageNotFoundException;
import com.terflo.helpdesk.model.factory.ImageFactory;
import com.terflo.helpdesk.model.repositories.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    private final ImageFactory imageFactory;


    public ImageService(ImageRepository imageRepository, ImageFactory imageFactory) {
        this.imageRepository = imageRepository;
        this.imageFactory = imageFactory;
    }


    public Image getImage(Long id) throws ImageNotFoundException {
        if(id == null) throw new ImageNotFoundException("Изображение не найдено");
        return imageRepository
                .findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Изображение не найдено"));
    }

    public Long saveImage(MultipartFile file) throws IOException {
        return imageRepository.save(imageFactory.getImage(file)).getId();
    }

    public Long saveImage(Image image) {
        return imageRepository.save(image).getId();
    }

    @Transactional
    public void updateImage(Long id, MultipartFile file) throws ImageNotFoundException, IOException {
        Image image = imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Изображение не найдено"));
        image.setBytes(file.getBytes());
        imageRepository.save(image);
    }

    @Transactional
    public void deleteImage(Long id) throws ImageNotFoundException {
        if(id == null)
            return;
        if(!imageRepository.findById(id).isPresent())
            throw new ImageNotFoundException("Изображение не найдено");
        else
            imageRepository.deleteById(id);
    }

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
