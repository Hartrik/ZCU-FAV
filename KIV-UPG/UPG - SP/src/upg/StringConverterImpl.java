package upg;

import java.text.DecimalFormat;
import java.text.ParseException;
import javafx.util.StringConverter;

/**
 * Upravený StringConverter, podporuje desetinnou čárku i tečku.
 *
 * @version 2016-03-24
 * @author Patrik Harag
 */
public class StringConverterImpl extends StringConverter<Double> {
    private final DecimalFormat df = new DecimalFormat("#.##");

    @Override
    public String toString(Double value) {
        return (value == null) ? "" : df.format(value);
    }

    @Override
    public Double fromString(String value) {
        if (value == null)
            return null;

        value = value.trim();
        if (value.length() < 1)
            return null;

        try {
            return df.parse(value.replace(".", ",")).doubleValue();
        } catch (ParseException ex) {
            return null;
        }
    }

}