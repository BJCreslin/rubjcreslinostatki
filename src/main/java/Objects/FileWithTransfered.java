package Objects;

import java.io.File;

public class FileWithTransfered extends FileWithOstatki {
    public FileWithTransfered() {
        nameFile=NAMEOUTFILE;
        file = new File(PATHTOFILES + nameFile);
    }
}
