package de.qr.calendar.controller.rest;

import de.qr.calendar.model.Eintrag;
import de.qr.calendar.model.Kalender;
import de.qr.calendar.qrcode.QrCodeGenerator;
import de.qr.calendar.repository.EintragRepository;
import de.qr.calendar.repository.KalenderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
@RequestMapping(path = "/api/kalender")
public class KalenderRestController {

    private KalenderRepository kalenderRepository;

    private EintragRepository eintragRepository;

    private QrCodeGenerator qrCodeGenerator;

    @Value("${app.server.domain}")
    private String serverDomain;

    @Autowired
    public KalenderRestController(KalenderRepository kalenderRepository,
                                  EintragRepository eintragRepository,
                                  QrCodeGenerator qrCodeGenerator) {
        this.kalenderRepository = kalenderRepository;
        this.eintragRepository = eintragRepository;
        this.qrCodeGenerator = qrCodeGenerator;
    }

    @GetMapping(path = "/all", produces = "application/json")
    public Iterable<Kalender> getAllKalender() {
        return kalenderRepository.findAll();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public Kalender getKalenderById(@PathVariable UUID id) {
        return kalenderRepository.findById(id)
                .orElseThrow();
    }

    @PostMapping(path = "/", produces = "application/json")
    public Kalender createKalender(@RequestBody Kalender kalender) {
        return kalenderRepository.save(kalender);
    }

    @PutMapping(path = "/{id}", produces = "application/json")
    public Kalender updateKalender(@RequestBody Kalender updKalender, @PathVariable UUID id) {
        return kalenderRepository.findById(id)
                .map(kalender -> {
                    kalender.setEmpfaenger(updKalender.getEmpfaenger());
                    kalender.setBezeichnung(updKalender.getBezeichnung());
                    kalender.setGueltigVon(updKalender.getGueltigVon());
                    kalender.setGueltigBis(updKalender.getGueltigBis());
                    return kalenderRepository.save(kalender);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteKalender(@PathVariable UUID id) {
        kalenderRepository.deleteById(id);
    }

    @PostMapping(path = "/{id}/eintrag", produces = "application/json")
    public Eintrag createEintrag(@RequestBody Eintrag eintrag, @PathVariable UUID id) {
        Kalender kalender = kalenderRepository.findById(id)
                .orElseThrow();
        eintrag.setKalender(kalender);
        return eintragRepository.save(eintrag);
    }

    @PutMapping(path = "/{kalenderId}/eintrag/{eintragId}", produces = "application/json")
    public Eintrag updateEintrag(@RequestBody Eintrag updEintrag,
                                 @PathVariable UUID kalenderId,
                                 @PathVariable UUID eintragId) {
        Kalender kalender = kalenderRepository.findById(kalenderId)
                .orElseThrow();
        return eintragRepository.findById(eintragId)
                .map(eintrag -> {
                    eintrag.setKalender(kalender);
                    eintrag.setNummer(updEintrag.getNummer());
                    eintrag.setBild(updEintrag.getBild());
                    eintrag.setInhalt(updEintrag.getInhalt());
                    eintrag.setLink(updEintrag.getLink());
                    eintrag.setAufrufbarAb(updEintrag.getAufrufbarAb());
                    return eintragRepository.save(eintrag);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{kalenderId}/eintrag/{eintragId}")
    public void deleteEintrag(@PathVariable UUID kalenderId, @PathVariable UUID eintragId) {
        Kalender kalender = kalenderRepository.findById(kalenderId)
                .orElseThrow();
        eintragRepository.deleteById(eintragId);
    }

    @GetMapping(path = "/zip/{kalenderId}", produces = "application/zip")
    public ResponseEntity<byte[]> getQrCodesAsZip(@PathVariable UUID kalenderId) {
        Kalender kalender = kalenderRepository.findById(kalenderId)
                .orElseThrow();

        byte[] zipFile = new byte[0];

        log.info("Erstelle zip-File für Kalender {} - {}", kalender.getId(), kalender.getBezeichnung());
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            var zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

            // einzelne QR-Dateien in Liste speichern
            List<File> qrCodeFiles = kalender.getEintraege().stream()
                    .map(eintrag -> {
                        String qrCodeUrl = serverDomain
                                + "eintrag"
                                + "qr?id="
                                + eintrag.getId();
                        String qrCodeFilename = eintrag.getNummer() + ".png";
                        Path qrDatei = qrCodeGenerator.createQrCodeAsImageFile(qrCodeUrl, qrCodeFilename);
                        return qrDatei.toFile();
                    })
                    .collect(Collectors.toList());
            log.info(qrCodeFiles.size() + " QR-Codes gefunden");
            for (File file : qrCodeFiles) {
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                FileInputStream fileInputStream = new FileInputStream(file);

                IOUtils.copy(fileInputStream, zipOutputStream);

                fileInputStream.close();
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
            zipFile = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity
                    .internalServerError()
                    .body(zipFile);
        }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + kalender.getBezeichnung() + ".zip\"")
                .body(zipFile);
    }
}
