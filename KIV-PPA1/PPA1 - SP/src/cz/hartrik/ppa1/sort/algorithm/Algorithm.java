package cz.hartrik.ppa1.sort.algorithm;

/**
 * Rozhraní pro řadící algoritmus.
 * <p>
 * Instance se váže ke konkrétním datům.
 * Pro řazení jiných dat je nutné vytvořit novou instanci.
 *
 * @see Data
 * @version 2015-10-18
 * @author Patrik Harag
 */
public interface Algorithm {

    /**
     * Provede další krok.
     * Tím se obvykle myslí nějaké prohození nebo posunutí.
     */
    public void nextStep();

    /**
     * Vrátí {@code true}, pokud jsou data seřazena.
     *
     * @return boolean
     */
    public boolean isSorted();

}