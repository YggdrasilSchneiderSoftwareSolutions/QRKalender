package de.qr.calendar;

import de.qr.calendar.model.Eintrag;
import de.qr.calendar.model.Kalender;
import de.qr.calendar.qrcode.QrCodeGenerator;
import de.qr.calendar.repository.EintragRepository;
import de.qr.calendar.repository.KalenderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
@Slf4j
@Profile("test")
class CalendarApplicationTests {

	@Autowired
	private KalenderRepository kalenderRepository;
	@Autowired
	private EintragRepository eintragRepository;
	@Autowired
	private QrCodeGenerator qrCodeGenerator;

	@Value("${file.storage.storage-location}")
	private String storageLocation;

	//@Test
	void testWorkflow() {
		Kalender kalender = new Kalender();
		kalender.setBezeichnung("Junit Testkalender");
		kalender.setEmpfaenger("Test");
		log.info("Speichere Kalender...");
		Kalender checkKalender = kalenderRepository.save(kalender);
		assert kalender.equals(checkKalender);
		log.info("Speichern erfolgreich " + kalender);

		Eintrag eintrag = new Eintrag();
		eintrag.setKalender(kalender);
		eintrag.setNummer(1);
		eintrag.setBild("bild/zu/gutschein.pdf");
		eintrag.setInhalt("Ein Eintrag mit einem Gutschein");
		eintrag.setLink("<p>http://einlink.de</p>");
		log.info("Speichere Eintrag");
		Eintrag checkEintrag = eintragRepository.save(eintrag);
		assert eintrag.equals(checkEintrag);
		log.info("Speichern erfolgreich " + eintrag);

		Eintrag eintrag2 = new Eintrag();
		eintrag2.setKalender(kalender);
		eintrag2.setNummer(2);
		eintrag2.setBild("link/zu/bild.png");
		eintrag2.setInhalt("Ein Eintrag mit einem Bild und einem Text");
		eintrag2.setLink("<p>http://einlink.de</p>");
		log.info("Speichere Eintrag");
		Eintrag checkEintrag2 = eintragRepository.save(eintrag2);
		assert eintrag2.equals(checkEintrag2);
		log.info("Speichern erfolgreich " + eintrag2);

		kalenderRepository.deleteById(kalender.getId());
	}

	//@Test
	void testQrCode() {
		qrCodeGenerator.createQrCodeAsImageFile("https://www.google.com", "Test.png");
		Path datei = Paths.get(System.getProperty("user.home"), storageLocation, "Test.png")
				.toAbsolutePath();
		log.info("Testdatei: " + datei);
		assert datei.toFile().exists();
	}

}
