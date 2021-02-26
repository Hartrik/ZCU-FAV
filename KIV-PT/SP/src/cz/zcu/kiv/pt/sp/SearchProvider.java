package cz.zcu.kiv.pt.sp;

import java.util.List;

/**
 * Rozhraní pro třídu umožnující hledání v textu.
 *
 * @version 2016-09-30
 * @author Patrik Harag
 */
public interface SearchProvider {

   /**
    * Vyhledá v textu určitá slova.
    *
    * @param word hledané slovo
    * @return výsledky
    */
    public List<SearchResult> search(String word);

}
