package de.qr.calendar.controller.web;

import de.qr.calendar.file.FileService;
import de.qr.calendar.model.Eintrag;
import de.qr.calendar.model.Kalender;
import de.qr.calendar.model.User;
import de.qr.calendar.repository.EintragRepository;
import de.qr.calendar.repository.KalenderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.FileSystems;
import java.util.*;

@Controller
@Slf4j
public class KalenderController {

    private KalenderRepository kalenderRepository;
    private EintragRepository eintragRepository;
    private FileService fileService;

    @Autowired
    public KalenderController(KalenderRepository kalenderRepository, EintragRepository eintragRepository,
                              FileService fileService) {
        this.kalenderRepository = kalenderRepository;
        this.eintragRepository = eintragRepository;
        this.fileService = fileService;
    }

    private User getUser(Authentication auth) {
        if (auth == null) return null;
        if (auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        return null;
    }

    @GetMapping("/kalender")
    public ModelAndView kalenderliste(Authentication auth) {
        User user = getUser(auth);
        return kalenderliste(user);
    }

    @GetMapping("/kalender/new")
    public ModelAndView editKalender(Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            Kalender kal = new Kalender();
            return kalender(user, kal);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);
    }


    @GetMapping("/kalender/{id}")
    public ModelAndView editKalender(@PathVariable(name = "id") UUID id, Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            Optional<Kalender> kalOpt = kalenderRepository.findById(id);
            if (kalOpt.isPresent()) {
                Kalender kal = kalOpt.get();
                if (!kal.getErstellerId().equals(user.getId())) {
                    // Aus Security-Gründen wird so getan, als ob der Kalender nicht existiert
                    return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
                }
                return kalender(user, kal);
            }
            return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/kalender")
    public ModelAndView saveKalender(@ModelAttribute Kalender kalender, Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            if (kalender.getId() != null) { // Kalender wird editiert
                Optional<Kalender> kalOpt = kalenderRepository.findById(kalender.getId());
                if (kalOpt.isPresent()) {
                    Kalender kal = kalOpt.get();
                    if (!kal.getErstellerId().equals(user.getId())) {
                        // Aus Security-Gründen wird so getan, als ob der Kalender nicht existiert
                        return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
                    }
                    kal.setEmpfaenger(kalender.getEmpfaenger());
                    kal.setBezeichnung(kalender.getBezeichnung());
                    kal.setErstellerId(user.getId());
                    kal.setGueltigVon(kalender.getGueltigVon());
                    kal.setGueltigBis(kalender.getGueltigBis());
                    kalender = kalenderRepository.save(kal);
                } else {
                    return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
                }
                log.info("Kalender '{}' editiert", kalender.getBezeichnung());
            } else { // Neuer Kalender
                kalender.setErstellerId(user.getId());
                kalender = kalenderRepository.save(kalender);
                log.info("Kalender '{}' angelegt", kalender.getBezeichnung());
            }
            // Zeigt wieder die Kalenderseite mit Eintragsliste
            return kalender(user, kalender);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);
    }

    private ModelAndView error(String message, HttpStatus status) {
        Map<String, Object> model = new HashMap<>();
        model.put("message", message);
        model.put("redirect", "/kalender");
        return new ModelAndView("errorpage", model, status);
    }

    private ModelAndView error(String message, HttpStatus status, String redirect) {
        Map<String, Object> model = new HashMap<>();
        model.put("message", message);
        model.put("redirect", redirect);
        return new ModelAndView("errorpage", model, status);
    }

    private ModelAndView kalenderliste(User user) {
        if (user != null) {
            Iterable<Kalender> allKalender = kalenderRepository.findAllByErstellerId(user.getId());
            Map<String, Object> model = new HashMap<>();
            model.put("allKalender", allKalender);
            return new ModelAndView("kalenderliste", model);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);
    }

    private ModelAndView kalender(User user, Kalender kalender) {
        if (user != null) {
            Map<String, Object> model = new HashMap<>();
            model.put("kalender", kalender);
            if (kalender.getId() != null) {
                model.put("eintraege", eintragRepository.findAllByKalenderOrderByNummer(kalender));
            } else {
                model.put("eintraege", new ArrayList<Eintrag>());
            }
            return new ModelAndView("kalender", model);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/kalender/{id}")
    public ModelAndView deleteKalender(@PathVariable(name = "id") UUID id, Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            Optional<Kalender> kal = kalenderRepository.findById(id);
            if (kal.isPresent()) {
                Kalender kalender = kal.get();
                if (kalender.getErstellerId().equals(user.getId())) {
                    kalenderRepository.deleteById(kalender.getId());
                } else {
                    return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
                }
            } else {
                return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
            }
        }
        return kalenderliste(user);
    }

    // Neuer Eintrag
    @GetMapping("/kalender/{id}/eintrag")
    public ModelAndView createEintrag(@PathVariable(name = "id") UUID id, Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            Optional<Kalender> kalOpt = kalenderRepository.findById(id);
            if (kalOpt.isPresent()) {
                Kalender kal = kalOpt.get();
                if (!kal.getErstellerId().equals(user.getId())) {
                    // Aus Security-Gründen wird so getan, als ob der Kalender nicht existiert
                    return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
                }
                Map<String, Object> model = new HashMap<>();
                model.put("kalender", kal);
                model.put("eintrag", new Eintrag());
                return new ModelAndView("edit_eintrag", model);
            }
            return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/kalender/{id}/eintrag/{eintragid}")
    public ModelAndView editEintrag(@PathVariable(name = "id") UUID id, @PathVariable(name = "eintragid") UUID eintragId, Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            Optional<Kalender> kalOpt = kalenderRepository.findById(id);
            if (kalOpt.isPresent()) {
                Kalender kal = kalOpt.get();
                if (!kal.getErstellerId().equals(user.getId())) {
                    // Aus Security-Gründen wird so getan, als ob der Kalender nicht existiert
                    return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
                }
                Optional<Eintrag> eintragOpt = eintragRepository.findById(eintragId);
                if (!eintragOpt.isPresent() || !eintragOpt.get().getKalender().equals(kal)) {
                    return error("Der Eintrag existiert nicht", HttpStatus.NOT_FOUND);
                }
                Map<String, Object> model = new HashMap<>();
                model.put("kalender", kal);
                model.put("eintrag", eintragOpt.get());
                return new ModelAndView("edit_eintrag", model);
            }
            return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/kalender/{id}/eintrag")
    public ModelAndView saveEintrag(@PathVariable(name = "id") UUID id, @ModelAttribute Eintrag eintrag, Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            Optional<Kalender> kalOpt = kalenderRepository.findById(id);
            if (kalOpt.isPresent()) {
                Kalender kal = kalOpt.get();
                if (!kal.getErstellerId().equals(user.getId())) {
                    // Aus Security-Gründen wird so getan, als ob der Kalender nicht existiert
                    return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
                }
            } else {
                return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
            }
            // Prüfe Eintrag
            if (eintrag.getId() != null && eintrag.getId().toString().length() == 36) {
                // Vorhandener Eintrag wird editiert
                Optional<Eintrag> eintragOpt = eintragRepository.findById(eintrag.getId());
                if (eintragOpt.isPresent()) {
                    Eintrag neu = eintragOpt.get();
                    // Checken, ob der Eintrag zum Kalender gehört
                    if (!neu.getKalender().equals(kalOpt.get())) {
                        return error("Der Eintrag existiert nicht", HttpStatus.NOT_FOUND);
                    }
                    neu.setKalender(kalOpt.get());
                    neu.setNummer(eintrag.getNummer());
                    neu.setAufrufbarAb(eintrag.getAufrufbarAb());
                    neu.setInhalt(eintrag.getInhalt());
                    neu.setLink(eintrag.getLink());
                    eintrag = eintragRepository.save(neu);
                } else {
                    return error("Der Eintrag existiert nicht", HttpStatus.NOT_FOUND);
                }
            } else {
                // Neuer Eintrag
                Eintrag neu = new Eintrag();
                neu.setKalender(kalOpt.get());
                neu.setNummer(eintrag.getNummer());
                neu.setAufrufbarAb(eintrag.getAufrufbarAb());
                neu.setInhalt(eintrag.getInhalt());
                neu.setLink(eintrag.getLink());
                eintrag = eintragRepository.save(neu);
            }
            kalOpt = kalenderRepository.findById(id);
            // Nach dem Speichern bleiben wir im Eintrag
            Map<String, Object> model = new HashMap<>();
            model.put("kalender", kalOpt.get());
            model.put("eintrag", eintrag);
            return new ModelAndView("edit_eintrag", model);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);

    }

    @DeleteMapping("/kalender/{id}/eintrag/{eintragid}")
    public ModelAndView deleteEintrag(@PathVariable(name = "id") UUID id, @PathVariable(name = "eintragid") UUID eintragId, Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            Optional<Kalender> kalOpt = kalenderRepository.findById(id);
            if (kalOpt.isPresent()) {
                Kalender kal = kalOpt.get();
                if (!kal.getErstellerId().equals(user.getId())) {
                    // Aus Security-Gründen wird so getan, als ob der Kalender nicht existiert
                    return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
                }
                Optional<Eintrag> eintragOpt = eintragRepository.findById(eintragId);
                if (!eintragOpt.isPresent() || !eintragOpt.get().getKalender().equals(kal)) {
                    return error("Der Eintrag existiert nicht", HttpStatus.NOT_FOUND);
                }
                return kalender(user, kal);
            }
            return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);
    }

    @PostMapping(path = "/kalender/{id}/eintrag/{eintragid}/uploadfile")
    public ModelAndView handleFileUpload(@PathVariable(name = "id") UUID kalenderId, @PathVariable(name = "eintragid") UUID eintragId, @RequestParam("file") MultipartFile file, Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            Optional<Kalender> kalOpt = kalenderRepository.findById(kalenderId);
            Eintrag eintrag;
            if (kalOpt.isPresent()) {
                Kalender kal = kalOpt.get();
                if (!kal.getErstellerId().equals(user.getId())) {
                    // Aus Security-Gründen wird so getan, als ob der Kalender nicht existiert
                    return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
                }
                Optional<Eintrag> eintragOpt = eintragRepository.findById(eintragId);
                if (!eintragOpt.isPresent() || !eintragOpt.get().getKalender().equals(kal.getId())) {
                    return error("Der Eintrag existiert nicht", HttpStatus.NOT_FOUND);
                }
                eintrag = eintragOpt.get();
                // Datei in Ordner des Kalenders speichern
                log.info("Speichere Datei: {} für Kalender-ID {} zur Eintrag-ID{}",
                        file.getOriginalFilename(), kalenderId.toString(), eintragId.toString());
                fileService.saveFile(kalenderId, file);
                // Dateiname in Eintrag speichern
                eintrag = eintragRepository.findById(eintragId).orElseThrow();
                eintrag.setBild(file.getOriginalFilename());
                Eintrag updEintrag = eintragRepository.save(eintrag);

                Map<String, Object> model = new HashMap<>();
                model.put("kalender", kalOpt.get());
                model.put("eintrag", updEintrag);
                return new ModelAndView("edit_eintrag", model);
            }
            return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/kalender/{id}/eintrag/{eintragid}/deletefile")
    public ModelAndView deleteFile(@PathVariable(name = "id") UUID kalenderId, @PathVariable(name = "eintragid") UUID eintragId, @RequestParam("file") MultipartFile file, Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            Optional<Kalender> kalOpt = kalenderRepository.findById(kalenderId);
            if (kalOpt.isPresent()) {
                Kalender kal = kalOpt.get();
                if (!kal.getErstellerId().equals(user.getId())) {
                    // Aus Security-Gründen wird so getan, als ob der Kalender nicht existiert
                    return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
                }
                Optional<Eintrag> eintragOpt = eintragRepository.findById(eintragId);
                if (!eintragOpt.isPresent() || !eintragOpt.get().getKalender().equals(kal.getId())) {
                    return error("Der Eintrag existiert nicht", HttpStatus.NOT_FOUND);
                }
                Eintrag eintrag = eintragOpt.get();
                Kalender kalender = eintrag.getKalender();
                // Datei auf dem Filesystem löschen
                String zielDatei = kalender.getId()
                        + FileSystems.getDefault().getSeparator()
                        + eintrag.getBild();
                log.info("Lösche Datei: " + zielDatei);
                fileService.deleteFile(zielDatei);
                // Datei am Eintrag in Datenbank löschen
                eintrag.setBild(null);
                eintrag = eintragRepository.save(eintrag);
                Map<String, Object> model = new HashMap<>();
                model.put("kalender", kalOpt.get());
                model.put("eintrag", eintrag);
                return new ModelAndView("edit_eintrag", model);
            }
            return error("Der Kalender existiert nicht", HttpStatus.NOT_FOUND);
        }
        return error("Erst anmelden!", HttpStatus.FORBIDDEN);
    }
}