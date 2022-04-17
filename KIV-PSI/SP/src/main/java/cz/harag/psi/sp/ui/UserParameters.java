package cz.harag.psi.sp.ui;

/**
 * @author Patrik Harag
 * @version 2020-05-22
 */
public class UserParameters {

    private final String user;
    private final String pass;

    public UserParameters(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
