package de.qr.calendar.controller;

import de.qr.calendar.model.Artikel;
import de.qr.calendar.repository.ArtikelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/artikel")
public class ArtikelController {

    @Autowired
    private ArtikelRepository artikelRepository;

    @GetMapping(path = "/{id}")
    public Artikel getSongById(@PathVariable Long id) {
        return artikelRepository.findById(id)
                .orElseThrow();
    }

    @PostMapping(path = "/", produces = "application/json")
    public Artikel createArtikel(@RequestBody Artikel artikel) {
        return artikelRepository.save(artikel);
    }

    @PutMapping(path = "/{id}", produces = "application/json")
    public Artikel updateArtikel(@RequestBody Artikel updArtikel, @PathVariable Long id) {
        return artikelRepository.findById(id)
                .map(artikel -> {
                    artikel.setEintrag(updArtikel.getEintrag());
                    artikel.setBild(updArtikel.getBild());
                    artikel.setInhalt(updArtikel.getInhalt());
                    return artikelRepository.save(artikel);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteArtikel(@PathVariable Long id) {
        artikelRepository.deleteById(id);
    }
}
