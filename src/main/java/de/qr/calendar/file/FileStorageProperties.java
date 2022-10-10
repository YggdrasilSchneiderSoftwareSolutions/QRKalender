package de.qr.calendar.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.storage")
@Data
public class FileStorageProperties {
    private String storageLocation;
}
