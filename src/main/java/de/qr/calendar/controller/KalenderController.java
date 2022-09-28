package de.qr.calendar.controller;

import de.qr.calendar.model.Eintrag;
import de.qr.calendar.model.Kalender;
import de.qr.calendar.repository.EintragRepository;
import de.qr.calendar.repository.KalenderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/kalender")
public class KalenderController {

    private KalenderRepository kalenderRepository;

    private EintragRepository eintragRepository;

    @Autowired
    public KalenderController(KalenderRepository kalenderRepository,
                              EintragRepository eintragRepository) {
        this.kalenderRepository = kalenderRepository;
        this.eintragRepository = eintragRepository;
    }

    @GetMapping(path = "/all", produces = "application/json")
    public Iterable<Kalender> getAllKalender() {
        return kalenderRepository.findAll();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public Kalender getKalenderById(@PathVariable UUID id) {
        return kalenderRepository.findById(id)
                .orElseThrow();
    }

    @PostMapping(path = "/", produces = "application/json")
    public Kalender createKalender(@RequestBody Kalender kalender) {
        return kalenderRepository.save(kalender);
    }

    @PutMapping(path = "/{id}", produces = "application/json")
    public Kalender updateKalender(@RequestBody Kalender updKalender, @PathVariable UUID id) {
        return kalenderRepository.findById(id)
                .map(kalender -> {
                    kalender.setEmpfaenger(updKalender.getEmpfaenger());
                    kalender.setBezeichnung(updKalender.getBezeichnung());
                    kalender.setGueltigVon(updKalender.getGueltigVon());
                    kalender.setGueltigBis(updKalender.getGueltigBis());
                    return kalenderRepository.save(kalender);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteKalender(@PathVariable UUID id) {
        kalenderRepository.deleteById(id);
    }

    @PostMapping(path = "/{id}/eintrag", produces = "application/json")
    public Eintrag createEintrag(@RequestBody Eintrag eintrag, @PathVariable UUID id) {
        Kalender kalender = kalenderRepository.findById(id)
                .orElseThrow();
        eintrag.setKalender(kalender);
        return eintragRepository.save(eintrag);
    }

    @PutMapping(path = "/{kalenderId}/eintrag/{eintragId}", produces = "application/json")
    public Eintrag updateEintrag(@RequestBody Eintrag updEintrag,
                                 @PathVariable UUID kalenderId,
                                 @PathVariable UUID eintragId) {
        Kalender kalender = kalenderRepository.findById(kalenderId)
                .orElseThrow();
        return eintragRepository.findById(eintragId)
                .map(eintrag -> {
                    eintrag.setKalender(kalender);
                    eintrag.setNummer(updEintrag.getNummer());
                    eintrag.setBild(updEintrag.getBild());
                    eintrag.setInhalt(updEintrag.getInhalt());
                    eintrag.setLink(updEintrag.getLink());
                    eintrag.setAufrufbarAb(updEintrag.getAufrufbarAb());
                    return eintragRepository.save(eintrag);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{kalenderId}/eintrag/{eintragId}")
    public void deleteEintrag(@PathVariable UUID kalenderId, @PathVariable UUID eintragId) {
        Kalender kalender = kalenderRepository.findById(kalenderId)
                .orElseThrow();
        eintragRepository.deleteById(eintragId);
    }
}
