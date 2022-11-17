package de.qr.calendar.controller.rest;

import de.qr.calendar.model.Eintrag;
import de.qr.calendar.repository.EintragRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(path = "/api/eintrag")
public class EintragRestController {

    @Autowired
    private EintragRepository eintragRepository;

    @GetMapping(path = "/{id}")
    public Eintrag getEintragById(@PathVariable UUID id) {
        // TODO: Prüfung, ob aufrufbarAb schon erreicht ist und ggf. Fehler als Device liefern
        return eintragRepository.findById(id)
                .orElseThrow();
    }
}
