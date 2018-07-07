package cz.harag.bit.c04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.harag.bit.c03.Ciphers;
import org.junit.Test;

/**
 *
 * @author Patrik Harag
 */
public class CiphersTest {

    @Test
    public void c01() throws Exception {
        String c = new String(Files.readAllBytes(Paths.get("src/test/resources/c04/sifra1.txt")), "UTF-8");
        for (int i = 0; i < 26; i++) {
            System.out.println(Ciphers.caesar(i, c));
        }
        // kdyz-ses-narodil-vsichni-se-radovali-a-ty-jsi-plakal-zij-tak-aby-az-umres-vsichni-plakali-a-ty-jsi-byl-stastny
    }

    @Test
    public void c02() throws Exception {
        String c = new String(Files.readAllBytes(Paths.get("src/test/resources/c04/sifra2.txt")), "UTF-8");
        System.out.println(Ciphers.atbas(c));
        // jak»lvove»bijem»o»mrize»jak»lvove»v»kleci»jati»my»bychom»vzhuru»k»nebesum»a»jsme»zde»zemi»spjati»nam»zda»se»z»hvezd»ze»vane»hlas»nuz»pojdte»pani»blize»jen»trochu»blize»hrdobci»jimz»hrouda»nohy»vize»my»prijdem»odpust»maticko»jiz»jsi»nam»zeme»mala»my»blesk»k»myslenkam»sprahame»a»noha»parou»cvala»my»prijdem»duch»nas»roste»v»vys»a»tepny»touhou»biji»zimnicni»touhou»po»svetech»div»srdce»nerozbiji»my»prijdem»bliz»my»prijdem»bliz»my»svetu»dozijeme»my»bijem»o»mriz»ducha»lvi»a»my»ji»rozbijemeÑ
    }


    class Entry {
        public char c; public double v;
    }

    private char[] asArray(List<Entry> entries) {
        entries.sort(Comparator.comparingDouble((Entry e) -> e.v).reversed());

        char[] freqs = new char[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            freqs[i] = entries.get(i).c;
        }
        return freqs;
    }

    private List<Entry> freqs(String text) {
        Map<Character,Integer> frequencies = new HashMap<>();
        for (char ch : text.toCharArray())
            frequencies.put(ch, frequencies.getOrDefault(ch, 0) + 1);

        List<Entry> entries = new ArrayList<>();
        frequencies.forEach((character, integer) -> {
            if (character < 'a' || character > 'z') {
                System.out.println("ignore: " + character);
                return;
            }

            entries.add(new Entry() {{
                c = character;
                v = (double) integer / text.length();
            }});
        });
        return entries;
    }

    private char[] freqsCZ() throws IOException {
        String c = new String(Files.readAllBytes(Paths.get("src/test/resources/c04/frequency_cz_ref.txt")), "UTF-8");

        List<Entry> entries = new ArrayList<>();
        for (String s : c.split("\n")) {
            String[] split = s.split(" ");
            System.out.println(split[0] + " / " + split[1]);
            Entry entry = new Entry() {{ c = split[0].charAt(0); v = Double.parseDouble(split[1]); }};
            entries.add(entry);
        }

        return asArray(entries);
    }

    private char[] freqsEN() throws IOException {
        String c = new String(Files.readAllBytes(Paths.get("src/test/resources/c04/frequency_en_ref.txt")), "UTF-8");

        List<Entry> entries = new ArrayList<>();
        for (String s : c.split("\n")) {
            s = s.replace("  '", "");
            s = s.replace("':", "");
            String[] split = s.split(" ");
            Entry entry = new Entry() {{ c = split[0].charAt(0); v = Double.parseDouble(split[1]); }};
            entries.add(entry);
        }

        return asArray(entries);
    }

    @Test
    public void c03() throws Exception {
        String original = new String(Files.readAllBytes(Paths.get("src/test/resources/c04/sifra3.txt")), "UTF-8");
        StringBuilder out = new StringBuilder(original);


        List<Entry> freqs = freqs(original);

        freqs.forEach(entry -> System.out.println(entry.c + " / " + entry.v));
        char[] freqsArray = asArray(freqs);

        char[] charsCZ = freqsCZ();
        for (int i = 0; i < Math.min(charsCZ.length, freqsArray.length); i++) {
            char cz = charsCZ[i];
            char f = freqsArray[i];

            System.out.println(f + " -> " + cz);


            for (int j = 0; j < original.length(); j++) {
                char cur = original.charAt(j);
                if (cur == f) {
                    out.setCharAt(j, cz);
                }
            }
        }

        System.out.println(original);
        System.out.println(out);
    }

    @Test
    public void c03_b() throws Exception {
        String original = new String(Files.readAllBytes(Paths.get("src/test/resources/c04/sifra3.txt")), "UTF-8");
        StringBuilder out = new StringBuilder(original);


        List<Entry> freqs = freqs(original);

        freqs.forEach(entry -> System.out.println(entry.c + " / " + entry.v));
        char[] freqsArray = asArray(freqs);

        char[] charsCZ = freqsEN(); //freqsCZ();
        for (int i = 0; i < Math.min(charsCZ.length, freqsArray.length); i++) {
            char cz = charsCZ[i];
            char f = freqsArray[i];

            System.out.println(f + " -> " + cz);


            for (int j = 0; j < original.length(); j++) {
                char cur = original.charAt(j);
                if (cur == f) {
                    out.setCharAt(j, cz);
                }
            }
        }

        System.out.println(original);
        System.out.println(out);
    }

}
