package cz.zcu.kiv.pro.tree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 *
 * @version 2016-11-21
 * @author Patrik Harag
 */
public class LayoutManager {

    private static final LayoutProvider[] providers = {
        new LayoutProvider("Simple Layout", SimpleLayout::new),
        new LayoutProvider("Advanced Layout", AdvancedLayout::new),
        new LayoutProvider("Advanced Layout (Optimized)", AdvancedLayoutOpt::new),
    };

    public static class LayoutProvider {
        private final String name;
        private final BiFunction<Double, Double, Layout> f;

        public LayoutProvider(String name, BiFunction<Double, Double, Layout> f) {
            this.name = name;
            this.f = f;
        }

        public Layout createInstance(double w, double h) {
            return f.apply(w, h);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static List<LayoutProvider> getLayoutProviders() {
        return Collections.unmodifiableList(Arrays.asList(providers));
    }

    private LayoutManager() {
    }

}