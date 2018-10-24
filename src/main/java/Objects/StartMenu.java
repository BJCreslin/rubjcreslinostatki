package Objects;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;

public class StartMenu implements ActionMenu {
    String pathtoFiles = "\\\\Post\\креслин владимир\\перемещение\\";
    String nameMainFile = "Копия ПЕРЕМЕЩЕНИЯ.xls";
    String nameOstatkiCentralnyFile = "центральный.xls";
    String nameOstatkiBaza8File = "выставкасовп.xls";
    String outFile = "out.txt";
    int stolbecWithData;


    @Override
    public void action() {


        // Read XSL file
        try (FileInputStream inputStreamMain = new FileInputStream(new File(pathtoFiles + nameMainFile));
             FileInputStream inputStreamCentralnyFile = new FileInputStream(new File(pathtoFiles + nameOstatkiCentralnyFile));
             FileInputStream inputstreamBaza8File = new FileInputStream((new File(pathtoFiles + nameOstatkiBaza8File)));
             FileWriter fileOutTXT = new FileWriter(pathtoFiles + outFile, false)) {
            // Get the workbook instance for XLS file
            HSSFWorkbook workbookMain = new HSSFWorkbook(inputStreamMain);
            HSSFWorkbook workbookCentral = new HSSFWorkbook(inputStreamCentralnyFile);
            HSSFWorkbook workbookBaza8 = new HSSFWorkbook(inputstreamBaza8File);

            // Get first sheet from the workbook
            HSSFSheet sheetMain = workbookMain.getSheetAt(0);
            HSSFSheet sheetCentral = workbookCentral.getSheetAt(0);
            HSSFSheet sheetBaza8 = workbookBaza8.getSheetAt(0);

            stolbecWithData = stolbecWithDateDefine(sheetMain);





            fileOutTXT.write("1");
            fileOutTXT.write("2");
            System.out.println("All done");
            fileOutTXT.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //todo здесь должен быть основной
    }

    private int stolbecWithDateDefine(HSSFSheet sheetMain) {
        //визуально из файла взято
        return 14;
    }
}
