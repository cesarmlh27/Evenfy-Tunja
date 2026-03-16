package org.jdc.tunja_evenfy.rest;

import lombok.extern.slf4j.Slf4j;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(ApiPaths.API_V1 + "/upload")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class UploadRest {

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            log.info("Directorio de uploads listo: {}", uploadDir);
        } catch (IOException e) {
            log.error("No se pudo crear el directorio de uploads", e);
        }
    }

    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Tipo de archivo no permitido. Use: JPEG, PNG, WebP o GIF");
        }

        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;

        try {
            Path target = Paths.get(uploadDir).resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Imagen subida: {}", filename);

            String imageUrl = "/uploads/" + filename;
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (IOException e) {
            log.error("Error guardando imagen", e);
            throw new BadRequestException("Error al guardar la imagen");
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }
}
