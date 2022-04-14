package com.terflo.helpdesk.model.factories;

import com.google.common.io.ByteStreams;
import com.terflo.helpdesk.model.entity.Image;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Component
public class ImageFactory {

    private final static List<String> allowedTypes = Arrays.asList("image/png", "image/jpg", "image/jpeg");

    private final static String START_DATA_HASH = "base64,";
    private final static String START_DATA_TYPE = "data:";
    private final static String END_DATA_TYPE = ";";

    public Image getImage(MultipartFile file) throws IOException {
        if(!allowedTypes.contains(file.getContentType()))
            throw new IOException("Не поддерживаемый формат");
        else
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

    public Image getImage(InputStream inputStream, String filetype) throws IOException {
        return new Image(
                null,
                ByteStreams.toByteArray(inputStream),
                filetype
        );
    }

    public Image getImage(String base64WithType) throws IOException {

        String contentType = base64WithType.substring(START_DATA_TYPE.length(), base64WithType.indexOf(END_DATA_TYPE));
        String base64Hash = base64WithType.substring(base64WithType.indexOf(START_DATA_HASH) + START_DATA_HASH.length());

        if(!allowedTypes.contains((contentType)))
            throw new IOException("Не поддерживаемый формат");
        else
            return new Image(
                    null,
                    Base64.getDecoder().decode(base64Hash.getBytes(StandardCharsets.UTF_8)),
                    contentType
            );
    }
}
