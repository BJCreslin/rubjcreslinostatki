package Command;

import Objects.*;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ReadXLSFileCommand {
    private TransfertMap transfertMap;
    private FileWithOstatkiCentralSklad fileWithOstatkiCentralSklad;
    private FileWithOstatkiBaza8Sklad fileWithOstatkiBaza8Sklad;
    private FileWithNeededBaza8Sklad fileWithNeededBaza8Sklad;
    private ArrayList<Item> itemArrayList;
    private HashMap<Integer, Integer> centralMap;
    private HashMap<Integer, Integer> vystavkaMap;
    double percentToMake=0.6;
    double percentToMovement=0.7;


    public ReadXLSFileCommand(double percentToMake, TransfertMap transfertMap, FileWithOstatkiCentralSklad fileWithOstatkiCentralSklad,
                              FileWithOstatkiBaza8Sklad fileWithOstatkiBaza8Sklad,
                              FileWithNeededBaza8Sklad fileWithNeededBaza8Sklad) {
        this.transfertMap = transfertMap;
        this.fileWithOstatkiCentralSklad = fileWithOstatkiCentralSklad;
        this.fileWithOstatkiBaza8Sklad = fileWithOstatkiBaza8Sklad;
        this.fileWithNeededBaza8Sklad = fileWithNeededBaza8Sklad;
        itemArrayList = new ArrayList<>();
        this.percentToMake = percentToMake;
    }

    // Read XSL file
    public void readXLSFiles() {
        try (
                FileInputStream inputStreamMain = new FileInputStream(String.valueOf(fileWithNeededBaza8Sklad));
                FileInputStream inputStreamCentralnyFile = new FileInputStream(String.valueOf(fileWithOstatkiCentralSklad));
                FileInputStream inputstreamBaza8File = new FileInputStream(String.valueOf(fileWithOstatkiCentralSklad))) {

            // Get the workbook instance for XLS file
            HSSFWorkbook workbookMain = new HSSFWorkbook(inputStreamMain);
            HSSFWorkbook workbookCentral = new HSSFWorkbook(inputStreamCentralnyFile);
            HSSFWorkbook workbookBaza8 = new HSSFWorkbook(inputstreamBaza8File);

            // Get first sheet from the workbook
            HSSFSheet sheetMain = workbookMain.getSheetAt(0);
            HSSFSheet sheetCentral = workbookCentral.getSheetAt(0);
            HSSFSheet sheetBaza8 = workbookBaza8.getSheetAt(0);

            int stolbecWithData = stolbecWithDateDefine(sheetMain); //определяем номер столбца с данными по количеству в ячейке
            int stolbecWithCode = stolbecWithCodeDefine(sheetMain); //по коду
            int stolbecWithName = stolbecWithNameDefine(sheetMain); //по названию
            int stolbecWithMakeItem = stolbecMakeDefine(sheetMain); //по названию


            ArrayList<Item> itemArrayList = fillNeededOstatki(sheetMain); // заполняем Лист данными С нужными остатками

            centralMap = fillMap(sheetCentral); //заполняем мапу данными по остаткам на складе Центральный
            vystavkaMap = fillMap(sheetBaza8);  //Заполняем мапу данными по остаткам на скалде Выставка

            transfertMap.setTransferMap(fillResultMap());  //Заполняем резульитрующую мапу для перемещений

            transfertMap.setMakeMap(fillMakeFileMap());

            //записываем результат в файл
            //    outResult = new outResultFile(pathtoFiles + outFile);
            //    outResult.action(numberToShift);

            //     if (!makeFileMap.isEmpty()) {
            //         outMakeItem = new outResultFile(pathtoFiles + makeItemsFile);
            //         outMakeItem.action(makeFileMap);
            //      }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private HashMap<Item, Integer> fillMakeFileMap() {
        /**
         *  @return Возвращает словарь с количеством которое надо сделать.
         *  Если количество на центральном и на выставке меньше percentToMake (в долях) от необходимого
         */
        HashMap<Item, Integer> makeFileMap = new HashMap<>();

        StartMenu.dublicatecode(makeFileMap, itemArrayList, vystavkaMap, centralMap, percentToMake);
        return makeFileMap;
    }

    /***
     *
     * @param sheetMain
     * @return ArrayList<Item>   Возвращает список нужных на складе выставкаСовп данных
     */

    private ArrayList<Item> fillNeededOstatki(HSSFSheet sheetMain) {
        ArrayList<Item> itemArrayList = new ArrayList<>();
        boolean nalichie = true;
        int i = 1;
        while (nalichie) {
            boolean propuskaem = false;
            HSSFRow hssfRowData = sheetMain.getRow(i++);
            int code = 0;
            try {
                code = (int) hssfRowData.getCell(stolbecWithCodeDefine(sheetMain)).getNumericCellValue();
            } catch (IllegalStateException Illex) {
                propuskaem = true;
            } catch (NullPointerException Ex) {
                propuskaem = true;
                nalichie = false;
            }

            String name = "";
            try {
                name = hssfRowData.getCell(stolbecWithNameDefine(sheetMain)).getStringCellValue();
            } catch (Exception NullpointerException) {
                propuskaem = true;
            }

            int numberNeed = 0; // нужное количество
            try {
                numberNeed = (int) hssfRowData.getCell(stolbecWithDateDefine(sheetMain)).getNumericCellValue();

            } catch (NullPointerException npe) {
                propuskaem = true;
            } catch (IllegalStateException npe) {
                propuskaem = true;
            }
            if ((!propuskaem) && (numberNeed > 0)) {
                boolean isMakeItem = false;
                String stringMarker = "";

                try {
                    stringMarker = hssfRowData.getCell(stolbecMakeDefine(sheetMain)).getStringCellValue().toLowerCase();
                    if (stringMarker.equals("make")) {
                        isMakeItem = true;
                    }
                    itemArrayList.add(new Item(code, name, numberNeed, isMakeItem));
                } catch (Exception NullpointerException) {
                    itemArrayList.add(new Item(code, name, numberNeed));
                }
            }
        }
        return itemArrayList;
    }


    /****
     * фУНКЦИЯ ВЫЧИСЛЯЮЩАЯ КОЛИЧЕСТВО ТОВАРА ДЛЯ ПЕРЕМЕЩЕНИЯ в percentToMovement  (в долях) от нужного
     * @return HashMap<Item,Integer>  ЭЛЕМЕНТ, КОЛИЧЕСТВО НЕОБХОДИМОЕ ДЛЯ ПЕРЕМЕЩЕНИЯ
     */

    private HashMap<Item, Integer> fillResultMap() {
        HashMap<Item, Integer> mapForResult = new HashMap<>();
        for (Item item : itemArrayList) {

            try {
                int delta = item.getCount() - vystavkaMap.get(item.getCode());

                if (((1.0 * delta / (double) item.getCount()) > 0.3) &&
                        (centralMap.get(item.getCode()) > 0) &&
                        (delta > 0) && (item.getCount() > 0)
                        && (((1.0 * delta / (1.0 * vystavkaMap.get(item.getCode()))) > percentToMovement))) {

                    int numberForShift = (centralMap.get(item.getCode()) >= delta) ? delta : centralMap.get(item.getCode());
                    mapForResult.put(item, numberForShift);

                }
            } catch (Exception exc) {
                // log.warning(item.toString());
            }

        }
        return mapForResult;
    }

    /**
     * Формирует словарь на сонове таблицы xls
     *
     * @param sheet
     * @return HashMap<Integer, Integer>   Возвращает словарь- код , количество
     */

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
            } catch (NullPointerException npe) {
                propuskaem = true;
                flagToStop = true;
            }

            int number = 0;
            try {
                number = (int) sheet.getRow(iPos + shiftV).getCell(stolbecNumber).getNumericCellValue();
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
        return 4;
    }

    private int stolbecMakeDefine(HSSFSheet sheetMain) {
        //визуально из файла взято
        return 5;
    }


}


