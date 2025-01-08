package rozkladbot.telegram.startup;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rozkladbot.constants.AppConstants;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.entities.Institute;
import rozkladbot.entities.User;
import rozkladbot.services.InstituteService;
import rozkladbot.services.UserService;
import rozkladbot.telegram.utils.files.reader.LocalFileReader;
import rozkladbot.utils.deserializers.InstituteDeserializer;
import rozkladbot.utils.deserializers.UserDeserializer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// TODO: Доделать мигратор для портирования старых данных в бд
@Component("migrator")
public class Migrator {
    private static final Logger logger = LoggerFactory.getLogger(Migrator.class);
    private final LocalFileReader localFileReader;
    private final UserDeserializer userDeserializer;
    private final InstituteDeserializer instituteDeserializer;
    private final InstituteService instituteService;
    private final UserService userService;

    @Autowired

    public Migrator(
            LocalFileReader localFileReader,
            UserDeserializer userDeserializer,
            InstituteDeserializer instituteDeserializer,
            InstituteService instituteService,
            UserService userService) {
        this.localFileReader = localFileReader;
        this.userDeserializer = userDeserializer;
        this.instituteDeserializer = instituteDeserializer;
        this.instituteService = instituteService;
        this.userService = userService;
    }

    @PostConstruct
    public void migrateOldFiles() {
        migrateInstitutes();
        migrateUsers();
    }

    private void migrateUsers() {
        if (Files.exists(Paths.get(AppConstants.USERS_FILE_NAME))) {
            logger.info(LoggingConstants.USERS_LIST_FILE_FOUND);
            String fileContent = localFileReader.readLocalFile(AppConstants.USERS_FILE_NAME);
            List<User> userList = userDeserializer.deserialize(fileContent);
            for (User user : userList) {
                userService.save(user);
            }
            logger.info(LoggingConstants.USERS_LIST_MIGRATION_FINISHED);
        }
    }

    private void migrateInstitutes() {
        if (Files.exists(Paths.get(AppConstants.GROUPS_FILE_NAME))) {
            logger.info(LoggingConstants.USERS_LIST_FILE_FOUND);
            String fileContent = localFileReader.readLocalFile(AppConstants.GROUPS_FILE_NAME);
            List<Institute> institutes = instituteDeserializer.deserialize(fileContent);
            for (Institute institute : institutes) {
                instituteService.save(institute);
            }
            logger.info(LoggingConstants.USERS_LIST_MIGRATION_FINISHED);
        }
    }
}
