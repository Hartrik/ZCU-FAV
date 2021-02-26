package cz.zcu.kiv.pt.sp;

import java.util.Set;

/**
 * Rozhraní pro slovník.
 *
 * @version 2016-09-28
 * @author Patrik Harag
 */
public interface Dictionary {

    /**
     * Testuje existenci určitého slova ve slovníku.
     *
     * @param word testované slovo
     * @return true | false
     */
    public boolean contains(String word);

    /**
     * Přidá slovo do slovníku.
     *
     * @param word nové slovo
     */
    public void add(String word);

    /**
     * Vrátí velikost slovníku.
     *
     * @return velikost slovníku
     */
    public int size();

    /**
     * Vrátí slovník jako neměnnou množinu.
     *
     * @return množina
     */
    public Set<String> asSet();

}