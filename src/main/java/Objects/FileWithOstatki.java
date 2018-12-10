package Objects;

import java.io.File;

public class FileWithOstatki {
    public String getPATHTOFILES() {
        return PATHTOFILES;
    }

    public static String PATHTOFILES = "\\\\Post\\креслин владимир\\перемещение\\";
    public static String NAMEXLSFILEWITHNEEDEDOSTATKIBAZA8 = "Копия ПЕРЕМЕЩЕНИЯ.xls";  // Файл с данными по вместимости ячеек
    public static String NAMEXLSFILEWITHOSTATKICENTRALNY = "центральный.xls";   //Файл с осттаками на складе Центральный
    public static String NAMEXLSFILEWITHOSTATKIBAZA8 = "выставкасовп.xls";   //Файл с остатками на выставке
    String outFile = "out.txt";   //файл для вывода результата. Что бы 1С понимала нормально . Формат utf-16 !!!!
    String makeItemsFile = "makeItem.txt";   //файл для вывода списка позиций, которые надо сделать.
    // Что бы 1С понимала нормально . Формат utf-16 !!!!

    File file;
    String nameFile;

    public File getFile() {
        return file;
    }


    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        if (file.isFile()) {
            return file.getName();
        }
        return nameFile;
    }
}
