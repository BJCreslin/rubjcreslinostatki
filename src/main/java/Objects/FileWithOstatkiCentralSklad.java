package Objects;

import java.io.File;

public class FileWithOstatkiCentralSklad extends FileWithOstatki {

    public FileWithOstatkiCentralSklad() {
        nameFile = NAMEXLSFILEWITHOSTATKICENTRALNY;
        file = new File(PATHTOFILES + nameFile);
    }


}
