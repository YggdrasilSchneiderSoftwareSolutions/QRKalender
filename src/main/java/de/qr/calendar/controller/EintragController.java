package de.qr.calendar.controller;

import de.qr.calendar.model.Eintrag;
import de.qr.calendar.repository.EintragRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/eintrag")
public class EintragController {

    @Autowired
    private EintragRepository eintragRepository;

    @GetMapping(path = "/{id}")
    public Eintrag getEintragById(@PathVariable UUID id) {
        // TODO: Prüfung, ob aufrufbarAb schon erreicht ist und ggf. Fehler als Device liefern
        return eintragRepository.findById(id)
                .orElseThrow();
    }
}
