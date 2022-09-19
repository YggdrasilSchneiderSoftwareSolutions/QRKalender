package de.qr.calendar.repository;

import de.qr.calendar.model.Eintrag;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EintragRepository extends CrudRepository<Eintrag, UUID> {
}
