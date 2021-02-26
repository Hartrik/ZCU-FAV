package cz.zcu.kiv.pt.sp;

import java.util.ArrayList;
import java.util.List;

/**
 * Třída představuje jeden uzel v trii.
 *
 * @author Michal-PC
 */
public class TrieNode {

    /**
     * ArrayList uchovavajici deti uzlu
     */
    private final List<TrieNode> childrens = new ArrayList<>();
    /**
     * ArrayList uchovavajici indexi slova v textu
     */
    private final List<SearchResult> results = new ArrayList<>();
    /**
     * Hodnota(znaky) uzlu
     */
    private String value;
    /**
     * Kolik ma uzel potomku
     */
    private int numberOfChildrens = 0;

    //------------------------Konstruktory------------------------------
    /**
     * Vytvaří
     *
     * @param value Hodnota uzlu
     */
    public TrieNode(String value) {
        this.value = value;
    }

    //------------------------Pristupove metody------------------------------

    /**
     * Vraci ArrayList potomku
     *
     * @return ArrayList potomku
     */
    public List<TrieNode> getChildrens() {
        return childrens;
    }

    /**
     * Vraci hodnotu uzlu
     *
     * @return String Hodnota uzlu
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Vraci pocet deti uzlu
     *
     * @return int Pocet deti
     */
    public int getNumberOfChildrens() {
        return this.numberOfChildrens;
    }

    /**
     * Vraci List indexu v textu
     *
     * @return List indexu
     */
    public List<SearchResult> getResults() {
        return results;
    }

    /**
     * Slouzi pro rozhodnuti pokud je konec slova nebo ne
     *
     * @return boolean Je/Neni
     */
    public boolean isEndOfWord() {
        return !results.isEmpty();
    }

    //------------------------Nastavovaci metody------------------------------

    /**
     * Prida potomka do ArrayListu potomku
     *
     * @param children Pridavany potomek
     */
    public void addChildren(TrieNode children) {
        childrens.add(children);
        this.numberOfChildrens++;
    }

    /**
     * Nastavuje hodnotu uzlu
     *
     * @param value Nova hodnota uzlu
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Nastavuje novy pocet deti
     *
     * @param number Pocet deti
     */
    public void setNumberOfChildrens(int number) {
        this.numberOfChildrens = number;
    }

    /**
     * Prida index slova v textu
     *
     * @param result Pocatecni a konecny index
     */
    public void addResult(SearchResult result) {
        results.add(result);
    }

}
