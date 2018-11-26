package Objects;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import view.outResult;
import view.outResultFile;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class StartMenu implements ActionMenu {
    private Logger log = Logger.getLogger(getClass().getName());

    String pathtoFiles = "\\\\Post\\креслин владимир\\перемещение\\";
    String nameMainFile = "Копия ПЕРЕМЕЩЕНИЯ.xls";  // Файл с данными по вместимости ячеек
    String nameOstatkiCentralnyFile = "центральный.xls";   //Файл с осттаками на складе Центральный
    String nameOstatkiBaza8File = "выставкасовп.xls";   //Файл с остатками на выставке
    String outFile = "out.txt";   //файл для вывода результата. Что бы 1С понимала нормально . Формат utf-16 !!!!
    String makeItemsFile = "makeItem.txt";   //файл для вывода списка позиций, которые надо сделать.
    // Что бы 1С понимала нормально . Формат utf-16 !!!!

    int stolbecWithData;
    int stolbecWithCode;
    int stolbecWithName;
    int stolbecWithMakeItem;

    ArrayList<Item> itemArrayList;
    HashMap<Integer, Integer> centralMap;
    HashMap<Integer, Integer> vystavkaMap;
    HashMap<Item, Integer> numberToShift;
    HashMap<Item, Integer> makeFileMap;


    outResult outResult;
    outResult outMakeItem;


    @Override

    public void action() {

        // Read XSL file
        try (FileInputStream inputStreamMain = new FileInputStream(new File(pathtoFiles + nameMainFile));
             FileInputStream inputStreamCentralnyFile = new FileInputStream(new File(pathtoFiles + nameOstatkiCentralnyFile));
             FileInputStream inputstreamBaza8File = new FileInputStream((new File(pathtoFiles + nameOstatkiBaza8File)))) {

            // Get the workbook instance for XLS file
            HSSFWorkbook workbookMain = new HSSFWorkbook(inputStreamMain);
            HSSFWorkbook workbookCentral = new HSSFWorkbook(inputStreamCentralnyFile);
            HSSFWorkbook workbookBaza8 = new HSSFWorkbook(inputstreamBaza8File);

            // Get first sheet from the workbook
            HSSFSheet sheetMain = workbookMain.getSheetAt(0);
            HSSFSheet sheetCentral = workbookCentral.getSheetAt(0);
            HSSFSheet sheetBaza8 = workbookBaza8.getSheetAt(0);

            stolbecWithData = stolbecWithDateDefine(sheetMain); //определяем номер столбца с данными по количеству в ячейке
            stolbecWithCode = stolbecWithCodeDefine(sheetMain); //по коду
            stolbecWithName = stolbecWithNameDefine(sheetMain); //по названию
            stolbecWithMakeItem = stolbecMakeDefine(sheetMain); //по названию


            itemArrayList = fillNeededOstatki(sheetMain); // заполняем Лист данными С нужными остатками

            log.fine("количество товара в листе" + itemArrayList.size() + "\n");

            centralMap = fillMap(sheetCentral); //заполняем мапу данными по остаткам на складе Центральный
            vystavkaMap = fillMap(sheetBaza8);  //Заполняем мапу данными по остаткам на скалде Выставка

            numberToShift = fillResultMap();  //Заполняем резульитрующую мапу для перемещений

            makeFileMap = fillMakeFileMap();

            //записываем результат в файл
            outResult = new outResultFile(pathtoFiles + outFile);
            outResult.action(numberToShift);

            if (!makeFileMap.isEmpty()) {
                outMakeItem = new outResultFile(pathtoFiles + makeItemsFile);
                outMakeItem.action(makeFileMap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private HashMap<Item, Integer> fillMakeFileMap() {
        /**
         *  @return Возвращает словарь с количеством которое надо сделать.
         *  Если количество на центральном и на выставке меньше 60% от необходимого
         */
        HashMap<Item, Integer> makeFileMap = new HashMap<>();

        for (Item item : itemArrayList) {
            if (item.isMake) {
                Integer itemOnVystavka = vystavkaMap.get(item.code);
                Integer itemOnCentral = centralMap.get(item.code);
                if (itemOnCentral == null) {
                    itemOnCentral = 0;
                }
                Integer itemNeed = item.count;

                if (0.6*itemNeed > ( (itemOnCentral + itemOnVystavka))) {
                    int numbr = itemNeed - itemOnCentral - itemOnVystavka;
                    makeFileMap.put(item, numbr);
                }
            }
        }
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

            int numberNeed = 0; // нужное количество
            try {
                numberNeed = (int) hssfRowData.getCell(stolbecWithData).getNumericCellValue();

            } catch (NullPointerException npe) {
                log.warning(i + " нет данных по количеству");
                propuskaem = true;
            } catch (IllegalStateException npe) {
                log.warning(i + " не числовые данные в ячейке");
                propuskaem = true;
            }
            if ((!propuskaem) && (numberNeed > 0)) {
                boolean isMakeItem = false;
                String stringMarker = "";

                try {
                    stringMarker = hssfRowData.getCell(stolbecWithMakeItem).getStringCellValue().toLowerCase();
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
     * фУНКЦИЯ ВЫЧИСЛЯЮЩАЯ КОЛИЧЕСТВО ТОВАРА ДЛЯ ПЕРЕМЕЩЕНИЯ
     * @return HashMap<Item                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               Integer>  ЭЛЕМЕНТ, КОЛИЧЕСТВО НЕОБХОДИМОЕ ДЛЯ ПЕРЕМЕЩЕНИЯ
     */

    private HashMap<Item, Integer> fillResultMap() {
        HashMap<Item, Integer> mapForResult = new HashMap<>();
        for (Item item : itemArrayList) {

            try {
                int delta = item.count - vystavkaMap.get(item.code);
                if (delta > 0) {
                    log.fine(item.toString() + " центр= " + centralMap.get(item.code) +
                            " выставка= " + vystavkaMap.get(item.code) + " delta " + delta + "\n");
                }


                if (((1.0 * delta / (double) item.count) > 0.3) &&
                        (centralMap.get(item.code) > 0) &&
                        (delta > 0) && (item.count > 0)
                        && (((1.0 * delta / (1.0 * vystavkaMap.get(item.code))) > 0.5))) {

                    int numberForShift = (centralMap.get(item.code) >= delta) ? delta : centralMap.get(item.code);
                    log.fine(item.toString() + " центр= " + centralMap.get(item.code) +
                            " выставка= " + vystavkaMap.get(item.code) + " delta " + delta + " shift " + numberForShift + "\n");
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
     * @return HashMap<Integer                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               Integer>   Возвращает словарь- код , количество
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
                log.warning("пропустили код");
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
