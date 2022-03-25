package com.terflo.helpdesk.model.services.interfaces;

import com.terflo.helpdesk.model.entity.Image;
import com.terflo.helpdesk.model.exceptions.ImageNotFoundException;

public interface ImageService {

    Image getImageByID(Long id) throws ImageNotFoundException;

    Image saveImage(Image image);

    Image updateImageByID(Long id, Image newImage) throws ImageNotFoundException;

    void deleteImageByID(Long id) throws ImageNotFoundException;

    void deleteImage(Image image) throws ImageNotFoundException;

}
