package rozkladbot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rozkladbot.entities.ScheduleForm;
import rozkladbot.services.ScheduleService;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/get")
    public String getSchedule(@ModelAttribute ScheduleForm scheduleForm, Model model) {
        System.out.println(model.asMap());
        return "schedule-list";
    }
}
