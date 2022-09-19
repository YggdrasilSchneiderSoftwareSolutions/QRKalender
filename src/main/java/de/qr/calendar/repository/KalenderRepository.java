package de.qr.calendar.repository;

import de.qr.calendar.model.Kalender;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface KalenderRepository extends CrudRepository<Kalender, UUID> {
}
