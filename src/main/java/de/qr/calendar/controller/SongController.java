package de.qr.calendar.controller;

import de.qr.calendar.model.Song;
import de.qr.calendar.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/song")
public class SongController {

    @Autowired
    private SongRepository songRepository;

    @GetMapping(path = "/{id}")
    public Song getSongById(@PathVariable Long id) {
        return songRepository.findById(id)
                .orElseThrow();
    }

    @PostMapping(path = "/", produces = "application/json")
    public Song createSong(@RequestBody Song song) {
        return songRepository.save(song);
    }

    @PutMapping(path = "/{id}", produces = "application/json")
    public Song updateSong(@RequestBody Song updSong, @PathVariable Long id) {
        return songRepository.findById(id)
                .map(song -> {
                    song.setEintrag(updSong.getEintrag());
                    song.setLink(updSong.getLink());
                    return songRepository.save(song);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteSong(@PathVariable Long id) {
        songRepository.deleteById(id);
    }
}
