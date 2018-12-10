package Objects;

import java.util.ArrayList;
import java.util.HashMap;

public class TransfertMap {
    public void setItemArrayList(ArrayList<Item> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    public void setLeftoversCentral(HashMap<Integer, Integer> leftoversCentral) {
        this.leftoversCentral = leftoversCentral;
    }

    public void setLeftoversbaza8(HashMap<Integer, Integer> leftoversbaza8) {
        this.leftoversbaza8 = leftoversbaza8;
    }

    ArrayList<Item> itemArrayList = new ArrayList<>();
    HashMap<Integer, Integer> leftoversCentral = new HashMap<>();
    HashMap<Integer, Integer> leftoversbaza8 = new HashMap<>();
    HashMap<Item, Integer> transferMap = new HashMap<>();
    HashMap<Item, Integer> makeMap = new HashMap<>();

    public void setMakeMap(HashMap<Item, Integer> makeMap) {
        this.makeMap = makeMap;
    }

    public void setTransferMap(HashMap<Item, Integer> transferMap) {
        this.transferMap = transferMap;
    }
}
