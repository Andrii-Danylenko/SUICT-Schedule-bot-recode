package rozkladbot.telegram.utils.migrator;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rozkladbot.constants.AppConstants;
import rozkladbot.entities.PairLink;
import rozkladbot.services.PairLinkService;
import rozkladbot.telegram.utils.files.reader.LocalFileReader;
import rozkladbot.utils.deserializers.PairLinksDeserializer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.LinkedList;

@Component("migratorImpl")
public class MigratorImpl implements Migrator {
    private static final Logger logger = LoggerFactory.getLogger(MigratorImpl.class);
    private final LocalFileReader localFileReader;
    private final PairLinkService pairLinkService;
    private final PairLinksDeserializer pairLinksDeserializer;

    public MigratorImpl(
            LocalFileReader localFileReader,
            PairLinkService pairLinkService,
            PairLinksDeserializer pairLinksDeserializer) {
        this.localFileReader = localFileReader;
        this.pairLinkService = pairLinkService;
        this.pairLinksDeserializer = pairLinksDeserializer;
    }

    @PostConstruct
    public void migrate() {
        logger.info(AppConstants.MIGRATION_STARTED);
        try {
            migratePairLinks();
            logger.info(AppConstants.MIGRATION_FINISHED);
        } catch (IOException e) {
            logger.error(AppConstants.MIGRATION_FAILED, e.getMessage());
        }
    }

    private void migratePairLinks() throws IOException {
        Path directory = Paths.get(AppConstants.GROUPS_LINKS_FOLDER_NAME);
        Deque<PairLink> pairLinks = new LinkedList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path file : stream) {
                try {
                    pairLinks.addAll(pairLinksDeserializer.deserialize(localFileReader.readLocalFile(file.toString())));
                } catch (RuntimeException e) {
                    logger.error(AppConstants.MIGRATION_FAILED, e.getMessage());
                }
            }
        }
        pairLinkService.saveAll(pairLinks);
    }
}
