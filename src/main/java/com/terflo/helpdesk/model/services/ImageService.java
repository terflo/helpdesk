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
        if(id == null) return null;
        return imageRepository
                .findById(id)
                .orElseThrow(() -> new ImageNotFoundException("Изображение не найдено"));
    }

    @Transactional
    public Long saveImage(MultipartFile file) throws IOException {
        return imageRepository.save(imageFactory.getImage(file)).getId();
    }

    @Transactional
    public void updateImage(Long id, MultipartFile file) throws ImageNotFoundException, IOException {
        Image image = imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Изображение не найдено"));
        image.setBytes(file.getBytes());
        imageRepository.save(image);
    }
}
