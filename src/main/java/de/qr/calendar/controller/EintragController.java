package de.qr.calendar.controller;

import de.qr.calendar.model.Eintrag;
import de.qr.calendar.repository.EintragRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/eintrag")
public class EintragController {

    @Autowired
    private EintragRepository eintragRepository;

    @GetMapping(path = "/{id}")
    public Eintrag getEintragById(@PathVariable Long id) {
        return eintragRepository.findById(id)
                .orElseThrow();
    }

    @PostMapping(path = "/", produces = "application/json")
    public Eintrag createEintrag(@RequestBody Eintrag eintrag) {
        return eintragRepository.save(eintrag);
    }

    @PutMapping(path = "/{id}", produces = "application/json")
    public Eintrag updateEintrag(@RequestBody Eintrag updEintrag, @PathVariable Long id) {
        return eintragRepository.findById(id)
                .map(eintrag -> {
                    eintrag.setKalender(updEintrag.getKalender());
                    eintrag.setNummer(updEintrag.getNummer());
                    eintrag.setBild(updEintrag.getBild());
                    eintrag.setInhalt(updEintrag.getInhalt());
                    eintrag.setLink(updEintrag.getLink());
                    return eintragRepository.save(eintrag);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteEintrag(@PathVariable Long id) {
        eintragRepository.deleteById(id);
    }
}
