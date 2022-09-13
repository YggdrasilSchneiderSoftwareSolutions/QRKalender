package de.qr.calendar.controller;

import de.qr.calendar.model.Kalender;
import de.qr.calendar.repository.KalenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/kalender")
public class KalenderController {

    @Autowired
    private KalenderRepository kalenderRepository;

    @GetMapping(path = "/all", produces = "application/json")
    public Iterable<Kalender> getAllKalender() {
        return kalenderRepository.findAll();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public Kalender getKalenderById(@PathVariable Long id) {
        return kalenderRepository.findById(id)
                .orElseThrow();
    }

    @PostMapping(path = "/", produces = "application/json")
    public Kalender createKalender(@RequestBody Kalender kalender) {
        return kalenderRepository.save(kalender);
    }

    @PutMapping(path = "/{id}", produces = "application/json")
    public Kalender updateKalender(@RequestBody Kalender updKalender, @PathVariable Long id) {
        return kalenderRepository.findById(id)
                .map(kalender -> {
                    kalender.setEmpfaenger(updKalender.getEmpfaenger());
                    kalender.setBezeichnung(updKalender.getBezeichnung());
                    return kalenderRepository.save(kalender);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteKalender(@PathVariable Long id) {
        kalenderRepository.deleteById(id);
    }
}
