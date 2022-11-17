package de.qr.calendar.controller.web;

import de.qr.calendar.model.Eintrag;
import de.qr.calendar.repository.EintragRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(path = "/eintrag")
public class EintragController {

    @Autowired
    private EintragRepository eintragRepository;

    @GetMapping("/qr")
    public String getEintragByScannedQrCode(@RequestParam(name = "id") String id, Model model) {
        model.addAttribute("id", id);
        return "eintrag";
    }
}
