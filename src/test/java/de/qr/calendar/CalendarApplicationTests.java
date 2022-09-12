package de.qr.calendar;

import de.qr.calendar.model.Artikel;
import de.qr.calendar.model.Eintrag;
import de.qr.calendar.model.Kalender;
import de.qr.calendar.model.Song;
import de.qr.calendar.repository.ArtikelRepository;
import de.qr.calendar.repository.EintragRepository;
import de.qr.calendar.repository.KalenderRepository;
import de.qr.calendar.repository.SongRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

@SpringBootTest
@Slf4j
class CalendarApplicationTests {

	@Autowired
	private KalenderRepository kalenderRepository;
	@Autowired
	private EintragRepository eintragRepository;
	@Autowired
	private ArtikelRepository artikelRepository;
	@Autowired
	private SongRepository songRepository;

	@Test
	void testWorkflow() {
		Kalender kalender = new Kalender();
		kalender.setBezeichnung("Junit Testkalender");
		kalender.setEmpfaenger("Test");
		log.info("Speichere Kalender...");
		Kalender checkKalender = kalenderRepository.save(kalender);
		assert kalender.equals(checkKalender);
		log.info("Speichern erfolgreich, Objekt angelegt");

		Eintrag eintrag = new Eintrag();
		eintrag.setKalender(kalender);
		eintrag.setNummer(1);
		log.info("Speichere Eintrag");
		Eintrag checkEintrag = eintragRepository.save(eintrag);
		assert eintrag.equals(checkEintrag);
		log.info("Speichern erfolgreich, Objekt angelegt");

		Eintrag eintrag2 = new Eintrag();
		eintrag2.setKalender(kalender);
		eintrag2.setNummer(2);
		log.info(eintrag2.toString());
		Eintrag checkEintrag2 = eintragRepository.save(eintrag2);
		assert eintrag2.equals(checkEintrag2);

		Artikel artikel = new Artikel();
		artikel.setEintrag(eintrag);
		artikel.setBild("pfad/zum/bild/junit.png");
		artikel.setInhalt("Das ist ein Text aus einem Junit-Test");
		Artikel checkArtikel = artikelRepository.save(artikel);
		assert artikel.equals(checkArtikel);

		Song song = new Song();
		song.setEintrag(eintrag);
		song.setLink("<p>http://junit-test.de</p>");
		Song checkSong = songRepository.save(song);
		assert song.equals(checkSong);

		kalenderRepository.deleteById(kalender.getId());
	}

}
