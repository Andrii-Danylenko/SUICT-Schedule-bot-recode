package rozkladbot.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import rozkladbot.entities.Institute;
import rozkladbot.entities.ScheduleForm;

import java.util.List;

@Controller
@RequestMapping("/login")
public class LoginController {
    private RestTemplate restTemplate;
    private final String restTemplateUrl;

    public LoginController(RestTemplate restTemplate, @Value("${server.link.2}") String restTemplateUrl) {
        this.restTemplate = restTemplate;
        this.restTemplateUrl = restTemplateUrl;

    }

    @GetMapping
    public String showScheduleForm(Model model) {
        ResponseEntity<List<Institute>> institutesResponse =
                restTemplate.exchange(restTemplateUrl + "/get/institutes/all",
                        HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                        });
        List<Institute> institutes = institutesResponse.getBody();
        model.addAttribute("scheduleForm", new ScheduleForm());
        model.addAttribute("institutes", institutes);
        return "schedule-form";
    }
}
