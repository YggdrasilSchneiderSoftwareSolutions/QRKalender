package de.qr.calendar.file;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
@Profile("test")
//@Disabled("Achtung, Test legt Testdatei an")
public class FileUploadServiceTest {

    @Autowired
    FileService uploadService;

    @Test
    @Disabled
    void testSaveFile() throws IOException {
        Path path = Paths.get("src/test/resources/test.txt");
        String name = "junittest.txt";
        String originalFileName = "test.txt";
        String contentType = "text/plain";
        byte[] content = null;
        content = Files.readAllBytes(path);

        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        // FIXME
        uploadService.saveFile(null, result);
    }

    @Test
    void testDeleteFile() {
        uploadService.deleteFile("test.txt");
    }
}
