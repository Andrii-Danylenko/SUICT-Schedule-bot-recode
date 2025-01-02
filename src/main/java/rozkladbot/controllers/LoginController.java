package rozkladbot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rozkladbot.entities.ScheduleForm;

@Controller
@RequestMapping("/login")
public class LoginController {
    @GetMapping
    public String showScheduleForm(Model model) {
        model.addAttribute("scheduleForm", new ScheduleForm());
        return "schedule-form";
    }
}
