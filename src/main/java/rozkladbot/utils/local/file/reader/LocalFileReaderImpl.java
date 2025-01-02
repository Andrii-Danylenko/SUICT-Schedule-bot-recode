package rozkladbot.utils.local.file.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rozkladbot.constants.LoggingConstants;
import rozkladbot.exceptions.LocalFileReadingFailedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class LocalFileReaderImpl implements LocalFileReader {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileReaderImpl.class);

    public String readLocalFile(String pathToFile) {
        try {
            logger.info(LoggingConstants.STARTED_READING_LOCAL_FILE_MESSAGE + pathToFile);
            return Files.readString(Path.of(pathToFile));
        } catch (IOException e) {
            logger.error(LoggingConstants.LOCAL_FILE_READING_FAILED_MESSAGE, e);
            throw new LocalFileReadingFailedException(e);
        }
    }

}
