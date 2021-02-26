package cz.zcu.kiv.pt.sp;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementace trie.
 *
 * @author Michal_Fiala
 */
public class TrieSearch implements SearchProvider {

    private final TrieNode root;
    /**
     * Vkladany cely text
     */
    private final String text;
    /**
     * Stack pro ukladani nedodelanych uzlu
     */
    private Stack<TrieNode> stack;
    /**
     * List pro ulozeni vysledku vyhledavani
     */
    private List<SearchResult> results;

    //------------------------Konstruktory------------------------------
    /**
     * Vytvori novy koren
     * Ulozi si vkladany text
     * Vyvola metodu pro vytvoreni Standartni Trie
     *
     * @param text Vkladany text
     */
    public TrieSearch(String text) {
        this.root = new TrieNode("");
        this.text = text;
        makeStandartTrie();
    }

    //------------------------vyhledavani------------------------------
    /**
     * Metoda pro vyhledani slova v komprimovane trii
     * Pokud nenalezne List bude prazdny
     *
     * @param word Hledane slovo
     * @return List indexu slova v textu
     */
    @Override
    public List<SearchResult> search(String word) {
        results = new ArrayList<>();
        TrieNode root = this.root;
        searchLevel(word, root);
        return results;
    }

    /**
     * Metoda pro prohledani potomku
     * Metoda se vola rekurzivne dokud nedojde do uzlu(listu), ktery nema zadneho potomka
     * Po nalezeni casti slova se o tuto cast slovo zmensi
     *
     * @param word Cele nebo cast slova
     * @param root Uzel, ze ktereho se koukame na jeho deti
     */
    private void searchLevel(String word, TrieNode root) {
        //Pokud ma vice nez 0 deti
        if (root.getNumberOfChildrens() > 0) {
            int pomIndex;
            String pomWord;
            boolean found;
            //Projde vsechny potomky dokud nenalezne prefix
            for (TrieNode children : root.getChildrens()) {
                pomIndex = 0;
                pomWord = "";
                found = false;
                //Prochazeni po znacich a porovnavani s aktualnim potomkem
                for (int i = 0; i < word.length(); i++) {
                    pomIndex = i;
                    pomWord = pomWord + word.charAt(i);
                    if (pomWord.equalsIgnoreCase(children.getValue())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    //Pokud se proslo cele slovo
                    if (pomIndex == (word.length() - 1)) {
                        this.results = children.getResults();
                    } //Vyvolani metody znovu, ale s nalezenym potomkem a zkracenym slovem
                    else {
                        searchLevel(word.substring(pomIndex + 1), children);
                    }
                }
            }
        } else {
            this.results = root.getResults();
        }
    }

    //------------------------Metody pro vytvoreni Trie------------------------------
    /**
     * Metoda pro vytvoreni standartni trie
     */
    private void makeStandartTrie() {
        Pattern pattern = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(text);
        //Bere kazde slovo z textu a jeho indexy v tomto poli a vlozi je do standartni trie
        while (matcher.find()) {
            insert(matcher.group(), matcher.start(), matcher.end());
        }
        //Ze standartni trie se vytvori komprimovana
        makeCompressedTrie();
    }

    /**
     * Metoda pro vytvoreni komprimovane trie
     */
    private void makeCompressedTrie() {
        stack = new Stack();
        TrieNode kRoot = root;
        //Vyvola metodu pro upraveni hlavniho korene
        changeOneRoot(kRoot);

    }

    //------------------------Pomocne metody pro vytvoreni Trie------------------------------
    /**
     * Metoda pro upraveni aktualniho korene
     * Metoda se vola rekurzivne dokud nenarazi na list
     * Pote bere uzly ze Stacku
     * Upravi uzel, ktery neni oznacen jako konec slova a ktery ma pouze jednoho naslednika
     * Upravi ho tak, ze sjednoti hodnotu uzlu a naslednika a prevezme deti naslednika
     *
     * @param root
     */
    private void changeOneRoot(TrieNode root) {
        int numberOfChildren = root.getNumberOfChildrens();
        //Pokud ma jedno dite, snazi se upravit tento uzel
        if (numberOfChildren == 1) {
            //Pokud je uz na nem konec slova(ulozen index) preskoci na dalsi dite
            if (root.isEndOfWord() || root.getValue().equalsIgnoreCase("")) {
                changeOneRoot(root.getChildrens().get(0));
            } //Pokud na nem neni ulozen index spoji pismena a vezme si indexy ditete
            else {
                TrieNode children = root.getChildrens().get(0);
                //Spojeni hodnot
                root.setValue(root.getValue() + children.getValue());
                //Prenastaveni deti
                root.getChildrens().clear();
                root.setNumberOfChildrens(0);
                //Pokud je dite konec slova, tak se konec slova posune na sjednoceny uzel
                for (SearchResult childrenResults : children.getResults()) {
                    root.addResult(childrenResults);
                }
                //Prenastaveni ukazatele na deti
                if (children.getNumberOfChildrens() > 0) {
                    for (TrieNode actual : children.getChildrens()) {
                        root.addChildren(actual);
                    }
                    changeOneRoot(root);
                } //Pokud je list, Stack.pop
                else if ((children.getNumberOfChildrens() == 0) && (!stack.empty())) {
                    changeOneRoot((TrieNode) stack.pop());
                }
            }
        } //Pokud je vice deti
        else if (numberOfChildren > 1) {
            //Stack.push vsechny deti
            for (TrieNode actual : root.getChildrens()) {
                stack.push(actual);
            }
            //A posledni pridane si vezmeme
            if (!stack.empty()) {
                changeOneRoot((TrieNode) stack.pop());
            }
        } //Pokud uz je list stack.pop
        else if ((numberOfChildren == 0) && (!stack.empty())) {
            changeOneRoot((TrieNode) stack.pop());
        }
    }

    /**
     * Metoda pro vlozeni slova do standartni trie
     *
     * @param input vkladane slovo
     * @param start startovni index v textu
     * @param end konecny index v textu
     */
    private void insert(String input, int start, int end) {
        TrieNode nRoot = root;
        boolean found;
        int i = 0;
        int length = input.length() - 1;
        String sub;
        boolean added = false;
        //Dokud se neprojde cele slovo
        while (!added) {
            sub = "" + input.charAt(i);
            found = false;
            //Projde vsechny deti stavajiciho korene pokud je nalezeno existujici
            //Presune se na to ktere ma stejnou hodnotu jako pismeno
            for (TrieNode children : nRoot.getChildrens()) {
                //pokud nalezne dite se stejnou hodnotou
                if (children.getValue().equalsIgnoreCase(sub)) {
                    nRoot = children;
                    found = true;
                    break;
                }
            }
            //Pokud neni nalezeno prida se jako potomek stavajiciho korene
            if (!found) {
                TrieNode addNode = new TrieNode(sub);
                nRoot.addChildren(addNode);
                nRoot = addNode;
            }
            //Pokud se projde cele slovo
            if (i == length) {
                nRoot.addResult(new SearchResult(start, end));
                added = true;
            }
            i++;

        }
    }
    //------------------------Pomocne metody------------------------------

    /**
     * Vypise cely strom
     *
     * @param tmpRoot Koren stromu
     */
    public void printTree(TrieNode tmpRoot) {

        Queue<TrieNode> currentLevel = new LinkedList<TrieNode>();
        Queue<TrieNode> nextLevel = new LinkedList<TrieNode>();

        currentLevel.add(tmpRoot);

        while (!currentLevel.isEmpty()) {
            Iterator<TrieNode> iter = currentLevel.iterator();
            while (iter.hasNext()) {
                TrieNode currentNode = iter.next();
                for (TrieNode node : currentNode.getChildrens()) {
                    nextLevel.add(node);
                }
                System.out.print(currentNode.getValue() + "-");
            }
            System.out.print(" | ");
            System.out.println();
            currentLevel = nextLevel;
            nextLevel = new LinkedList<TrieNode>();
        }
    }

}
