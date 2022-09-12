package de.qr.calendar.repository;

import de.qr.calendar.model.Song;
import org.springframework.data.repository.CrudRepository;

public interface SongRepository extends CrudRepository<Song, Long> {
}
