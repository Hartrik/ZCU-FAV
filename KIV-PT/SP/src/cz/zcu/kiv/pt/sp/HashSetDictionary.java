package cz.zcu.kiv.pt.sp;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementace slovníku pomocí HashSet.
 *
 * @version 2016-09-28
 * @author Patrik Harag
 */
public class HashSetDictionary implements Dictionary {

    private final Set<String> data = new HashSet<>();

    @Override
    public void add(String word) {
        data.add(word);
    }

    @Override
    public boolean contains(String word) {
        return data.contains(word);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Set<String> asSet() {
        return Collections.unmodifiableSet(data);
    }

}