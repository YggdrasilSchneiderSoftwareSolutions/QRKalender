package de.qr.calendar.repository;

import de.qr.calendar.model.Artikel;
import org.springframework.data.repository.CrudRepository;

public interface ArtikelRepository extends CrudRepository<Artikel, Long> {
}
