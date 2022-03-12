package com.terflo.helpdesk.model.factories;

import com.terflo.helpdesk.model.entity.Image;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@Component
public class ImageFactory {

    private final static List<String> allowedTypes = Arrays.asList("image/png", "image/jpg", "image/jpeg");

    public Image getImage(MultipartFile file) throws IOException {
        return new Image(
                null,
                file.getBytes(),
                file.getContentType()
        );
    }

    public Image getImage(File file) throws IOException {
        if(!allowedTypes.contains(Files.probeContentType(file.toPath())))
            throw new IOException("Не поддерживаемый формат");
        else
            return new Image(
                null,
                Files.readAllBytes(file.toPath()),
                Files.probeContentType(file.toPath())
        );
    }
}
