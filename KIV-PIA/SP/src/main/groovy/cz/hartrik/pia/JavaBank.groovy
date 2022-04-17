package cz.hartrik.pia

/**
 * Utility class.
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
class JavaBank {

    /**
     * JavaBank code.
     */
    static final String CODE = '1024'

    static final int ACCOUNT_CODE_SIZE = 4
    static final int ACCOUNT_NUMBER_SIZE = 10
    static final int LOGIN_SIZE = 8
    static final int PASSWORD_SIZE = 4

    /**
     * Generates random account number with bank code.
     *
     * @param random
     * @return account number
     */
    static String generateAccountNumber(Random random) {
        def number = generateRandomNumber(random, ACCOUNT_NUMBER_SIZE)
        def code = generateRandomNumber(random, ACCOUNT_CODE_SIZE)
        number + '/' + code
    }

    /**
     * Generates random password.
     *
     * @param random
     * @return password
     */
    static String generateRandomPassword(Random random) {
        generateRandomNumber(random, PASSWORD_SIZE)
    }

    /**
     * Generates random login.
     *
     * @param random
     * @return login
     */
    static String generateRandomLogin(Random random) {
        generateRandomNumber(random, LOGIN_SIZE)
    }

    private static String generateRandomNumber(Random random, int digits) {
        def chars = '0123456789'.chars
        return (0..<digits).collect { chars[random.nextInt(chars.size())] }.join('')
    }
}
