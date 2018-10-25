package Objects;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import sun.rmi.runtime.Log;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class StartMenu implements ActionMenu {
    private Logger log = Logger.getLogger(getClass().getName());

    String pathtoFiles = "\\\\Post\\креслин владимир\\перемещение\\";
    String nameMainFile = "Копия ПЕРЕМЕЩЕНИЯ.xls";
    String nameOstatkiCentralnyFile = "центральный.xls";
    String nameOstatkiBaza8File = "выставкасовп.xls";
    String outFile = "out.txt";
    int stolbecWithData;
    int stolbecWithCode;
    int stolbecWithName;

    ArrayList<Item> itemArrayList;
    HashMap<Integer, Integer> centralMap;
    HashMap<Integer, Integer> vystavkaMap;
    HashMap<Item, Integer> numberToShift;


    @Override

    public void action() {
        itemArrayList = new ArrayList<>();

        // Read XSL file
        try (FileInputStream inputStreamMain = new FileInputStream(new File(pathtoFiles + nameMainFile));
             FileInputStream inputStreamCentralnyFile = new FileInputStream(new File(pathtoFiles + nameOstatkiCentralnyFile));
             FileInputStream inputstreamBaza8File = new FileInputStream((new File(pathtoFiles + nameOstatkiBaza8File)));
             //  FileWriter fileOutTXT = new FileWriter(pathtoFiles + outFile, false));
             OutputStreamWriter fileOutTXT = new OutputStreamWriter(new FileOutputStream(pathtoFiles + outFile, false),
                     "UTF-16")) {
            // Get the workbook instance for XLS file
            HSSFWorkbook workbookMain = new HSSFWorkbook(inputStreamMain);
            HSSFWorkbook workbookCentral = new HSSFWorkbook(inputStreamCentralnyFile);
            HSSFWorkbook workbookBaza8 = new HSSFWorkbook(inputstreamBaza8File);

            // Get first sheet from the workbook
            HSSFSheet sheetMain = workbookMain.getSheetAt(0);
            HSSFSheet sheetCentral = workbookCentral.getSheetAt(0);
            HSSFSheet sheetBaza8 = workbookBaza8.getSheetAt(0);

            stolbecWithData = stolbecWithDateDefine(sheetMain);
            stolbecWithCode = stolbecWithCodeDefine(sheetMain);
            stolbecWithName = stolbecWithNameDefine(sheetMain);

            boolean nalichie = true;
            int i = 1;
            while (nalichie) {
                boolean propuskaem = false;
                HSSFRow hssfRowData = sheetMain.getRow(i++);
                int code = 0;
                try {
                    code = (int) hssfRowData.getCell(stolbecWithCode).getNumericCellValue();
                } catch (IllegalStateException Illex) {
                    propuskaem = true;
                    log.warning(i + " не цифровой код товара или группы");
                } catch (NullPointerException Ex) {
                    propuskaem = true;
                    nalichie = false;
                }

                String name = "";
                try {
                    name = hssfRowData.getCell(stolbecWithName).getStringCellValue();
                } catch (Exception NullpointerException) {
                    propuskaem = true;
                    log.warning(i + "нет названия товара");
                }

                int numberNeed = 0;
                try {
                    numberNeed = (int) hssfRowData.getCell(stolbecWithData).getNumericCellValue();

                } catch (NullPointerException npe) {
                    log.warning(i + " нет данных по количеству");
                    propuskaem = true;
                }
                if ((!propuskaem) && (numberNeed > 0)) {
                    itemArrayList.add(new Item(code, name, numberNeed));
                }
            }
            log.warning("количество товара в листе" + itemArrayList.size() + "\n");

            centralMap = fillMap(sheetCentral);
            log.warning("number of Central " + centralMap.size());
            vystavkaMap = fillMap(sheetBaza8);
            log.warning("number of Vystavka " + vystavkaMap.size());
            numberToShift = new HashMap<>();


            for (Item item : itemArrayList) {

                try {
                    int delta = item.count - vystavkaMap.get(item.code);
                    if (delta > 0) {
                        log.warning(item.toString() + " центр= " + centralMap.get(item.code) +
                                " выставка= " + vystavkaMap.get(item.code) + " delta " + delta + "\n");
                    }


                    if (((1.0 * delta / (double) item.count) > 0.3) &&
                            (centralMap.get(item.code) > 0) &&
                            (delta > 0) && (item.count > 0)
                            && (((1.0 * delta / (1.0 * vystavkaMap.get(item.code))) > 0.5)))


                    {

                        int numberForShift = (centralMap.get(item.code) >= delta) ? delta : centralMap.get(item.code);
                        log.warning(item.toString() + " центр= " + centralMap.get(item.code) +
                                " выставка= " + vystavkaMap.get(item.code) + " delta " + delta + " shift " + numberForShift + "\n");
                        numberToShift.put(item, numberForShift);

                    }
                } catch (Exception exc) {
                    // log.warning(item.toString());
                }

            }

            if (numberToShift.size() > 0) {
                fileOutTXT.write("счет=10\n" +
                        "Дата=2018-10-23 11:57:51\n" +
                        "Клиент=8\n");
                for (Map.Entry<Item, Integer> entry : numberToShift.entrySet()) {
                    fileOutTXT.write("Следтовар\n");
                    fileOutTXT.write("КОЛИчество=" + entry.getValue() + "\n");
                    fileOutTXT.write("Кодтовара=" + entry.getKey().code + "\n");
                    fileOutTXT.write("Цена=308\n");
                }
            }


//            fileOutTXT.write("1");
//            fileOutTXT.write("2");
//            System.out.println("All done");
            fileOutTXT.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //todo здесь должен быть основной
    }

    private HashMap<Integer, Integer> fillMap(HSSFSheet sheet) {
        HashMap<Integer, Integer> itemHashMap = new HashMap<>();
        boolean flagToStop = false;
        int iPos = 0;
        int shiftV = 7;
        int stolbecCode = centralStolbecCodeFind();
        int stolbecNumber = centralStolbecNumberFind();


        while (!flagToStop) {
            int code = 0;
            boolean propuskaem = false;
            try {
                code = (int) sheet.getRow(iPos + shiftV).getCell(stolbecCode).getNumericCellValue();
            } catch (IllegalStateException Illex) {
                propuskaem = true;
                flagToStop = true;
                log.warning("пропустили код");
            } catch (NullPointerException npe) {
                propuskaem = true;
                flagToStop = true;
            }

            int number = 0;
            try {
                number = (int) sheet.getRow(iPos + shiftV).getCell(stolbecNumber).getNumericCellValue();
                if (code == 6119) {
                    log.warning("6119==== " + number);
                }
                if (code == 6039) {
                    log.warning("6039==== " + number);
                }
            } catch (NullPointerException npe) {
                propuskaem = true;
                flagToStop = true;
            }

            if ((number > 0) && (!flagToStop) && (!propuskaem)) {
                itemHashMap.put(code, number);
            }
            iPos++;
        }
        return itemHashMap;
    }


    private int centralStolbecNumberFind() {
        return 6;
    }

    private int centralStolbecCodeFind() {
        return 1;
    }

    private int stolbecWithNameDefine(HSSFSheet sheetMain) {
        return 1;
    }

    private int stolbecWithCodeDefine(HSSFSheet sheetMain) {
        return 0;
    }

    private int stolbecWithDateDefine(HSSFSheet sheetMain) {
        //визуально из файла взято
        return 13;
    }
}
