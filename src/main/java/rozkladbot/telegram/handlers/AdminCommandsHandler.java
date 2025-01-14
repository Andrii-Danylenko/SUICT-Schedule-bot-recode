package rozkladbot.telegram.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.constants.BotButtons;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.entities.User;
import rozkladbot.services.UserService;
import rozkladbot.telegram.factories.KeyBoardFactory;
import rozkladbot.telegram.shutdown.SessionShutdownManager;
import rozkladbot.telegram.utils.message.MessageBroadcaster;
import rozkladbot.telegram.utils.message.MessageSender;
import rozkladbot.telegram.utils.synchronization.LocalDataSynchronizer;

import java.util.List;

@Component("adminCommandsHandler")
public class AdminCommandsHandler {
    private final UserService userService;
    private final MessageSender messageSender;
    private final SessionShutdownManager sessionShutdownManager;
    private final LocalDataSynchronizer localDataSynchronizer;
    private final MessageBroadcaster messageBroadCaster;

    @Autowired
    public AdminCommandsHandler(
            UserService userService,
            MessageSender messageSender,
            SessionShutdownManager sessionShutdownManager,
            LocalDataSynchronizer localDataSynchronizer,
            MessageBroadcaster messageBroadCaster
    ) {
        this.userService = userService;
        this.messageSender = messageSender;
        this.sessionShutdownManager = sessionShutdownManager;
        this.localDataSynchronizer = localDataSynchronizer;
        this.messageBroadCaster = messageBroadCaster;
    }

    public void resolveStates(Update update, User user) {
        switch (user.getUserState()) {
            case AWAITING_USERS_LIST -> getAllConnectedUsers(update, user);
            case AWAITING_APPLICATION_TERMINATION -> terminateApplication(update, user);
            case AWAITING_DATA_SYNC -> synchronizeLocalData(update, user);
            case AWAITING_MESSAGE_SENDING -> sendBroadcastMessage(update);
        }
    }

    private void getAllConnectedUsers(Update update, User user) {
        List<User> connectedUsers = userService.getAll();
        if (connectedUsers.isEmpty()) {
            messageSender.sendMessage(
                    user,
                    BotMessageConstants.NO_CONNECTED_USERS_ARE_PRESENT_IN_DATABASE,
                    KeyBoardFactory.getCustomButton(BotButtons.BACK_TO_MENU, BotButtons.BACK_TO_MENU_DATA),
                    false,
                    update);
        } else {
            messageSender.sendMessage(
                    user,
                    BotMessageConstants.CONNECTED_USERS_LIST.formatted(connectedUsers.size()),
                    null,
                    false,
                    update);
        }
        for (User connectedUser : connectedUsers) {
            messageSender.sendMessage(
                    user,
                    connectedUser.toString().formatted(
                            connectedUser.getUsername(),
                            connectedUser.getId(),
                            connectedUser.getGroup().getName(),
                            connectedUser.getGroup().getCourse(),
                            connectedUser.getUserRole()
                    ),
                    null,
                    false,
                    update);
        }
    }

    private void terminateApplication(Update update, User user) {
        messageSender.sendMessage(user, BotMessageConstants.APP_TERMINATION_STARTED, null, false, update);
        sessionShutdownManager.shutdown();
        messageSender.sendMessage(user, BotMessageConstants.APP_TERMINATION_FINISHED, null, true, update);
    }

    private void synchronizeLocalData(Update update, User user) {
        messageSender.sendMessage(user, BotMessageConstants.LOCAL_DATA_SYNC_STARTED, null, false, update);
        boolean hasMessage = update.hasMessage();
        localDataSynchronizer.synchronize(hasMessage ? update.getMessage().getText() : "-all");
        messageSender.sendMessage(user, BotMessageConstants.LOCAL_DATA_SYNC_FINISHED, null, true, update);
    }

    private void sendBroadcastMessage(Update update) {
        messageBroadCaster.broadcast(update.getMessage().getText());
    }
}
