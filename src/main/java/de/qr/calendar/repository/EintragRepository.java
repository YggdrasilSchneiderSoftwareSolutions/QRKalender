package de.qr.calendar.repository;

import de.qr.calendar.model.Eintrag;
import de.qr.calendar.model.Kalender;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface EintragRepository extends CrudRepository<Eintrag, UUID> {

    List<Eintrag> findAllByKalenderOrderByNummer(Kalender kalender);

}
