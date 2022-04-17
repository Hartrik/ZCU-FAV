package cz.harag.psi.sp;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Represents connection. It is used by {@link POP3Client}.
 *
 * @author Patrik Harag
 * @version 2020-05-16
 */
public interface Connection extends AutoCloseable {

    BufferedReader getReader();

    BufferedWriter getWriter();

}
