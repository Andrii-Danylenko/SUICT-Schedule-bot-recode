package rozkladbot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.constants.BotButtons;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.Faculty;
import rozkladbot.entities.Group;
import rozkladbot.entities.Institute;
import rozkladbot.entities.User;
import rozkladbot.enums.UserState;
import rozkladbot.services.FacultyService;
import rozkladbot.services.GroupService;
import rozkladbot.services.InstituteService;
import rozkladbot.services.UserService;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.utils.message.MessageSender;
import rozkladbot.telegram.utils.message.MessageUtils;

import java.util.Comparator;
import java.util.List;

@Component("registrationHandler")
public class RegistrationHandler {
    private final MessageSender messageSender;
    private final InstituteService instituteService;
    private final FacultyService facultyService;
    private final GroupService groupService;
    private final UserService userService;

    @Autowired
    public RegistrationHandler(
            MessageSender messageSender,
            InstituteService instituteService,
            FacultyService facultyService,
            GroupService groupService,
            UserService userService) {
        this.messageSender = messageSender;
        this.instituteService = instituteService;
        this.facultyService = facultyService;
        this.groupService = groupService;
        this.userService = userService;
    }

    public User formUser(Update update, long chatId) {
        User user = new User();
        user.setUsername(getCorrectName(update));
        user.setId(chatId);
        user.setLastSentMessageId(update.getMessage().getMessageId());
        user.setUserState(UserState.AWAITING_GREETINGS);
        Group group = new Group();
        Institute institute = new Institute();
        Faculty faculty = new Faculty();
        faculty.setInstitute(institute);
        group.setFaculty(faculty);
        user.setGroup(group);
        return user;
    }

    public void registerUser(Update update, User user) {
        if (update.hasCallbackQuery()) {
            if (BotButtons.BACK_DATA.equals(update.getCallbackQuery().getData())) {
                resolveBackButton(update, user);
            }
            user.setLastSentMessageId(MessageUtils.getCorrectMessageIdWithOffset(update));
            switch (user.getUserState()) {
                case AWAITING_INSTITUTE -> getInstitute(update, user);
                case AWAITING_FACULTY -> getFaculty(update, user);
                case AWAITING_COURSE -> getCourse(update, user);
                case AWAITING_GROUP -> getGroup(update, user);
                case AWAITING_REGISTRATION_DATA_CONFIRMATION -> confirmRegistration(update, user);
            }
        }
    }

    private void getInstitute(Update update, User user) {
        if (update.hasCallbackQuery()) {
            if (BotButtons.BACK_DATA.equals(update.getCallbackQuery().getData())) {
                if (userService.existsById(user.getId())) {
                    user.setUserState(UserState.MAIN_MENU);
                    return;
                }
            }
        }
        List<Institute> instituteList = instituteService.getAll();
        messageSender.sendMessage(
                user,
                BotMessageConstants.USER_IS_UNREGISTERED + BotMessageConstants.INSTITUTE_SELECTION,
                KeyBoardFactory.getInstitutesKeyboardInline(instituteList),
                true, update);
        user.setUserState(UserState.AWAITING_FACULTY);
    }

    private void getFaculty(Update update, User user) {
        if (update.hasCallbackQuery()) {
            user.getGroup().getFaculty().getInstitute().setInstituteName(update.getCallbackQuery().getData());
        }
        List<Faculty> facultyList = facultyService.getFacultiesByInstituteName(update.getCallbackQuery().getData());
        messageSender.sendMessage(
                user,
                BotMessageConstants.USER_IS_UNREGISTERED + BotMessageConstants.FACULTY_SELECTION,
                KeyBoardFactory.getFacultiesKeyboardInline(facultyList),
                true, update);
        user.setUserState(UserState.AWAITING_COURSE);
    }

    private void getCourse(Update update, User user) {
        if (update.hasCallbackQuery()) {
            user.getGroup().getFaculty().setFacultyName(update.getCallbackQuery().getData());
        }
        List<String> courseList = groupService.getGroupCourses();
        courseList.sort(String::compareTo);
        messageSender.sendMessage(
                user,
                BotMessageConstants.USER_IS_UNREGISTERED + BotMessageConstants.COURSE_SELECTION,
                KeyBoardFactory.getCourseKeyboardInline(courseList),
                true, update);
        user.setUserState(UserState.AWAITING_GROUP);
    }

    private void getGroup(Update update, User user) {
        if (update.hasCallbackQuery()) {
            user.getGroup().setCourse(Long.parseLong(update.getCallbackQuery().getData()));
        }
        List<Group> groupList = groupService.findByFacultyAndCourse(user.getGroup().getFaculty().getFacultyName(), user.getGroup().getCourse());
        groupList.sort(Comparator.comparing(Group::getName));
        messageSender.sendMessage(
                user,
                BotMessageConstants.USER_IS_UNREGISTERED + BotMessageConstants.GROUP_SELECTION,
                KeyBoardFactory.getGroupKeyboardInline(groupList),
                true, update);
        user.setUserState(UserState.AWAITING_REGISTRATION_DATA_CONFIRMATION);
    }

    public void confirmRegistration(Update update, User user) {
        if (!update.hasCallbackQuery()) {
            return;
        }
        String callbackData = update.getCallbackQuery().getData();
        if (BotButtons.YES_DATA.equals(callbackData)) {
            Institute institute = instituteService.getByName(user.getGroup().getFaculty().getInstitute().getInstituteName());
            Faculty faculty = facultyService.getByName(user.getGroup().getFaculty().getFacultyName());
            Group group = groupService.getByName((user.getGroup().getName()));
            faculty.setInstitute(institute);
            group.setFaculty(faculty);
            user.setGroup(group);
            userService.save(user);
            user.setUserState(UserState.MAIN_MENU);
            user.setRegistered(true);
            messageSender.sendMessage(
                    user,
                    BotMessageConstants.REGISTRATION_SUCCESSFUL,
                    KeyBoardFactory.getCustomButton(BotButtons.BACK_TO_MENU, BotButtons.BACK_TO_MENU_DATA),
                    true, update);
        } else if (BotButtons.NO_DATA.equals(callbackData)) {
            user.setUserState(UserState.AWAITING_INSTITUTE);
            messageSender.sendMessage(
                    user,
                    BotMessageConstants.REGISTRATION_FAILED,
                    KeyBoardFactory.getCustomButton(BotButtons.TRY_AGAIN, BotButtons.TRY_AGAIN),
                    true, update);
        } else {
            user.getGroup().setName(update.getCallbackQuery().getData());
            messageSender.sendMessage(
                    user,
                    BotMessageConstants.CONFIRM_REGISTRATION_DATA.formatted(callbackData),
                    KeyBoardFactory.getYesOrNoInline(),
                    true, update);
        }
    }

    private void resolveBackButton(Update update, User user) {
        user.setLastSentMessageId(MessageUtils.getCorrectMessageIdWithOffset(update));
        switch (user.getUserState()) {
            case AWAITING_INSTITUTE -> {
                if (userService.existsById(user.getId())) {
                    user.setUserState(UserState.MAIN_MENU);
                }
            }
            case AWAITING_FACULTY,
                 AWAITING_COURSE,
                 AWAITING_GROUP,
                 AWAITING_REGISTRATION_DATA_CONFIRMATION -> user.setUserState(UserState.AWAITING_INSTITUTE);
        }
    }
    private String getCorrectName(Update update) {
        String userName = null;
        if (update.hasMessage()) {
            if (update.getMessage().isUserMessage()) {
                userName = "@" +  update.getMessage().getFrom().getUserName();
            }
            else if (update.getMessage().isGroupMessage() || update.getMessage().isSuperGroupMessage()) {
                userName = update.getMessage().getChat().getTitle();
            }
        }
        return userName == null ? "N/A" : userName;
    }
}
