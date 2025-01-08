package rozkladbot.telegram.utils.files.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rozkladbot.constants.AppConstants;
import rozkladbot.utils.date.DateUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static rozkladbot.constants.LoggingConstants.SCHEDULE_CREATE_IF_NOT_EXISTS;

@Component("localFileWriterImpl")
public class LocalFileWriterImpl implements LocalFileWriter {
    private final static Logger logger = LoggerFactory.getLogger(LocalFileWriterImpl.class);

    public void writeFile(Path directoryPath, String fileName, String data) throws IOException {
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        Path filePath = directoryPath.resolve(fileName);
        try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile(), false)) {
            outputStream.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }

    public boolean checkIfAlreadyWritten(Path directoryPath, String fileName, boolean isForced) {
        Path filePath = directoryPath.resolve(fileName);
        try {
            return isForced || DateUtils.instantToLocalDate(
                            Files.getLastModifiedTime(filePath).toInstant())
                    .isBefore(DateUtils.getToday().minusDays(1)) ||
                   AppConstants.MONDAY.equalsIgnoreCase(DateUtils.getFullDayName(DateUtils.getToday()));
        } catch (IOException exception) {
            logger.info(SCHEDULE_CREATE_IF_NOT_EXISTS, fileName);
            return true;
        }
    }
}
