package de.qr.calendar.repository;

import de.qr.calendar.model.Eintrag;
import org.springframework.data.repository.CrudRepository;

public interface EintragRepository extends CrudRepository<Eintrag, Long> {
}
