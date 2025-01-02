package rozkladbot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rozkladbot.entities.ScheduleForm;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    @PostMapping("/get")
    public String loadSchedule(@ModelAttribute ScheduleForm form, Model model) {
        return "schedule-list";
    }
}
