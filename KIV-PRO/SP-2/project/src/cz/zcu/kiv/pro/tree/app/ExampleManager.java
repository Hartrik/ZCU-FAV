package cz.zcu.kiv.pro.tree.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @version 2016-11-21
 * @author Patrik Harag
 */
public class ExampleManager {

    private static final Path examplesDir = Paths.get("examples");

    public static class Example {
        private final String name;
        private final Path path;

        public Example(Path path) {
            this.path = path;
            this.name = fileName(path);
        }

        private static String fileName(Path path) {
            return path.getFileName().toString().replaceAll("\\.\\w+", "");
        }

        public String getName() {
            return name;
        }

        public String loadCode() throws IOException {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public static List<Example> loadAllExamples() {
        try {
            return Files.list(examplesDir)
                    .filter(Files::isReadable)
                    .map(Example::new)
                    .collect(Collectors.toList());

        } catch (IOException ex) {
            return Collections.emptyList();
        }
    }

    private ExampleManager() {
    }

}