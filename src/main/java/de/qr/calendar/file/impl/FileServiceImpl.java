package de.qr.calendar.file.impl;

import de.qr.calendar.exception.CustomFileNotFoundException;
import de.qr.calendar.exception.StorageException;
import de.qr.calendar.file.FileStorageProperties;
import de.qr.calendar.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final Path rootLocation;

    @Autowired
    public FileServiceImpl(FileStorageProperties properties) {
        rootLocation = Paths.get(System.getProperty("user.home")
                + properties.getStorageLocation()).toAbsolutePath();
        log.info("rootLocation=" + rootLocation);
        if (!rootLocation.toFile().exists()) {
            log.info("lege Rootlokation an...");
            try {
                Files.createDirectory(rootLocation);
            } catch (IOException e) {
                throw new StorageException("Root-Directory kann nicht angelegt werden");
            }
        }
    }

    @Override
    public void saveFile(UUID kalenderId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("Datei ist leer - keine Speicherung!");
        }

        Path zielOrdner = rootLocation.resolve(Paths.get(kalenderId.toString()))
                .normalize()
                .toAbsolutePath();

        // Ordner für den Eintrag anlegen, falls nicht vorhanden (entspricht UUID als Name)
        if (!zielOrdner.toFile().exists()) {
            try {
                Files.createDirectory(zielOrdner);
            } catch (IOException e) {
                throw new StorageException("Zielordner konnte nicht angelegt werden", e);
            }
        }

        Path zielDatei = zielOrdner.resolve(Paths.get(file.getOriginalFilename()))
                .normalize()
                .toAbsolutePath();

        // Securitycheck
        if (!zielDatei.getParent().startsWith(rootLocation.toAbsolutePath())) {
            throw new StorageException("Keine Speicherung der Datei außerhalb des Zielverzeichnisses!");
        }

        try(InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, zielDatei, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Speichern der Datei fehlgeschlagen", e);
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Path zielDatei = rootLocation.resolve(filename);
            Files.delete(zielDatei);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resource getFileAsResource(String filename) {
        try {
            Path zielDatei = rootLocation.resolve(filename);
            Resource resource = new UrlResource(zielDatei.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new CustomFileNotFoundException("Datei nicht gefunden: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new CustomFileNotFoundException("Datei nicht gefunden: " + filename, e);
        }
    }
}
