package rozkladbot.telegram.utils.files.writer;

import java.io.IOException;
import java.nio.file.Path;

public interface LocalFileWriter {
    void writeFile(Path directoryPath, String fileName, String data) throws IOException;

    boolean checkIfAlreadyWritten(Path directoryPath, String fileName, boolean isForced);
}
