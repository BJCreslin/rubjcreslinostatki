package view;

import Command.ConsoleCommand;
import Objects.Item;

import java.io.*;
import java.util.Map;
import java.util.logging.Logger;

public class outResultFile implements outResult {
    private Logger log = Logger.getLogger(getClass().getName());
    String pathToFile;

    public outResultFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    @Override

    public void action(Map<Item, Integer> mapForOut) {
        try (OutputStreamWriter fileOutTXT = new OutputStreamWriter(new FileOutputStream(pathToFile, false),
                "UTF-16")) {
            if (mapForOut.size() > 0) {
                fileOutTXT.write("счет=10\n" +
                        "Дата="+ ConsoleCommand.dateOut()+"\n" +
                        "Клиент=8\n");
                for (Map.Entry<Item, Integer> entry : mapForOut.entrySet()) {
                    fileOutTXT.write("Следтовар\n");
                    fileOutTXT.write("КОЛИчество=" + entry.getValue() + "\n");
                    fileOutTXT.write("Кодтовара=" + entry.getKey().getCode() + "\n");
                    fileOutTXT.write("Цена=308\n");
                }
            }
            fileOutTXT.flush();
            log.fine("File " + pathToFile + " created normally");
        } catch (UnsupportedEncodingException e) {
            log.severe("Ошибка в кодировке файла записи");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            log.severe("Не могу создать файл результатов");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
