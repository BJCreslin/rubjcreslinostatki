package Objects;

public class Item {
    int code;
    String name;
    int count;
    boolean isMake;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMake() {
        return isMake;
    }

    public void setMake(boolean make) {
        isMake = make;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Item(int code, String name, int count) {

        this.code = code;
        this.name = name;
        this.count = count;
        this.isMake = false;
    }

    public Item(int code, String name, int count, boolean isMake) {

        this.code = code;
        this.name = name;
        this.count = count;
        this.isMake = isMake;
    }


    @Override
    public String toString() {
        return "Item{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", count=" + count +
                '}';
    }
}
