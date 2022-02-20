package com.terflo.helpdesk.model.factory;

import com.terflo.helpdesk.model.entity.Image;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ImageFactory {

    public Image getImage(MultipartFile file) throws IOException {
        return new Image(null, file.getBytes(), file.getContentType());
    }
}
