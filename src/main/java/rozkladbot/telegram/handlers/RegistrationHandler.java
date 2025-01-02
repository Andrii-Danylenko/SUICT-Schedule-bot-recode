package rozkladbot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.Faculty;
import rozkladbot.entities.Institute;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.services.FacultyService;
import rozkladbot.services.InstituteService;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.MessageSender;

import java.util.List;

@Component("registrationHandler")
public class RegistrationHandler {
    private final MessageSender messageSender;
    private final InstituteService instituteService;
    private final FacultyService facultyService;

    @Autowired
    public RegistrationHandler(MessageSender messageSender, InstituteService instituteService, FacultyService facultyService) {
        this.messageSender = messageSender;
        this.instituteService = instituteService;
        this.facultyService = facultyService;
    }
    public User formUser(Update update, long chatId) {
        User user = new User();
        user.setUsername(update.getMessage().getFrom().getUserName());
        user.setId(chatId);
        user.setLastSentMessageId(update.getMessage().getMessageId());
        user.setUserState(UserState.AWAITING_GREETINGS);
        return user;
    }
    public void registerUser(Update update, User user) {
        switch (user.getUserState()) {
            case UNREGISTERED -> getInstitute(update, user);
            case AWAITING_FACULTY -> getFaculty(update, user);
        }
    }
    public void getInstitute(Update update, User user) {
        List<Institute> instituteList = instituteService.getAll();
        messageSender.sendMessage(
                user,
                BotMessageConstants.USER_IS_UNREGISTERED + BotMessageConstants.INSTITUTE_SELECTION,
                KeyBoardFactory.getInstitutesKeyboardInline(instituteList),
                true);
        user.setUserState(UserState.AWAITING_FACULTY);
    }
    public void getFaculty(Update update, User user) {
        List<Faculty> facultyList = facultyService.getAll();
        messageSender.sendMessage(
                user,
                BotMessageConstants.USER_IS_UNREGISTERED + BotMessageConstants.FACULTY_SELECTION,
                KeyBoardFactory.getFacultiesKeyboardInline(facultyList),
                true);
        user.setUserState(UserState.AWAITING_FACULTY);
    }
    // TODO: доделать курсы и группы.
}
