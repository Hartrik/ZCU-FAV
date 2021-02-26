package cz.zcu.kiv.ti.sp.huffman;

/**
 *
 * @version 2016-10-27
 * @author Patrik Harag
 */
public class Item {

    private final int value;
    private final int frequency;
    private final String code;

    public Item(int value, int frequency, String code) {
        this.value = value;
        this.frequency = frequency;
        this.code = code;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

}
