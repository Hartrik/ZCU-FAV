package cz.hartrik.puzzle.net;

import cz.hartrik.puzzle.net.protocol.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author Patrik Harag
 * @version 2017-10-28
 */
public class ConnectionTest {

    private static final int DEFAULT_TIMEOUT = 1000;

    // LOG IN

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testLogIn() throws Exception {
        Connection connection = ConnectionProvider.connect();

        Future<LogInResponse> response = connection.sendLogIn("Test99");
        assertThat(response.get(), is(LogInResponse.OK));

        connection.close();
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testLogInNameTooLong() throws Exception {
        Connection connection = ConnectionProvider.connect();

        String n = "123456789012345678901234567890";
        Future<LogInResponse> response = connection.sendLogIn(n);
        assertThat(response.get(), is(LogInResponse.NAME_TOO_LONG));

        connection.close();
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testLogInWrongChars() throws Exception {
        Connection connection = ConnectionProvider.connect();

        Future<LogInResponse> response = connection.sendLogIn("Te\ts*/t");
        assertThat(response.get(), is(LogInResponse.UNSUPPORTED_CHARS));

        connection.close();
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testLogInTwice() throws Exception {
        Connection connection = ConnectionProvider.connect();

        Future<LogInResponse> response1 = connection.sendLogIn("Test99");
        assertThat(response1.get(), is(LogInResponse.OK));

        Future<LogInResponse> response2 = connection.sendLogIn("Other");
        assertThat(response2.get(), is(LogInResponse.ALREADY_LOGGED));

        connection.close();
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testLogInNameAlreadyInUse() throws Exception {
        try (Connection c1 = ConnectionProvider.connect()) {
            Future<LogInResponse> response1 = c1.sendLogIn("Test99");
            assertThat(response1.get(), is(LogInResponse.OK));

            try (Connection c2 = ConnectionProvider.connect()) {
                Future<LogInResponse> response2 = c2.sendLogIn("Test99");
                assertThat(response2.get(), is(LogInResponse.NAME_ALREADY_IN_USE));
            }
        }
    }

    // LOG OUT

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testLogOut() throws Exception {
        try (Connection connection = ConnectionProvider.connect()) {

            Future<LogInResponse> inRes = connection.sendLogIn("Test");
            assertThat(inRes.get(), is(LogInResponse.OK));

            Future<LogOutResponse> outRes = connection.sendLogOut();
            assertThat(outRes.get(), is(LogOutResponse.OK));
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testLogOutWithoutLogIn() throws Exception {
        try (Connection connection = ConnectionProvider.connect()) {
            Future<LogOutResponse> outRes = connection.sendLogOut();
            assertThat(outRes.get(), is(LogOutResponse.OK));
        }
    }

    // NEW GAME

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testNewGame() throws Exception {
        try (Connection connection = ConnectionProvider.connect()) {
            Future<LogInResponse> response = connection.sendLogIn("Nick");
            assertThat(response.get(), is(LogInResponse.OK));

            Future<NewGameResponse> game = connection.sendNewGame(10, 5);
            assertThat(game.get().getStatus(), is(NewGameResponse.Status.OK));
            assertThat(game.get().getGameID() >= 0, is(true));
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testNewGameNotLogged() throws Exception {
        try (Connection connection = ConnectionProvider.connect()) {
            Future<NewGameResponse> game = connection.sendNewGame(10, 5);
            assertThat(game.get().getStatus(), is(NewGameResponse.Status.NO_PERMISSIONS));
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testNewGameWrongFormat() throws Exception {
        try (Connection connection = ConnectionProvider.connect()) {
            Future<LogInResponse> response = connection.sendLogIn("Nick");
            assertThat(response.get(), is(LogInResponse.OK));

            List<String> messages = Arrays.asList(
                    "",
                    ",",
                    "10,-2",
                    "-1,2",
                    "1000000,20",
                    "1,10",
                    "10",
                    "test"
            );

            for (String message : messages) {
                Future<String> game = connection.sendMessageAndHook("GNW", message);
                assertThat(message, game.get().matches("-[12]"), is(true));
            }
        }
    }

    // JOIN

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testNewGameAndJoin() throws Exception {
        try (Connection connection = ConnectionProvider.connect()) {
            Future<LogInResponse> response = connection.sendLogIn("Nick");
            assertThat(response.get(), is(LogInResponse.OK));

            Future<NewGameResponse> game = connection.sendNewGame(10, 5);
            assertThat(game.get().getStatus(), is(NewGameResponse.Status.OK));
            assertThat(game.get().getGameID() >= 0, is(true));

            Future<JoinGameResponse> join = connection.sendJoinGame(game.get().getGameID());
            assertThat(join.get(), is(JoinGameResponse.OK));
        }
    }

    // GPL

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testPlayerList() throws Exception {
        try (Connection connection = ConnectionProvider.connect()) {
            Future<LogInResponse> response = connection.sendLogIn("Nick");
            assertThat(response.get(), is(LogInResponse.OK));

            Future<NewGameResponse> game = connection.sendNewGame(10, 5);
            assertThat(game.get().getStatus(), is(NewGameResponse.Status.OK));
            assertThat(game.get().getGameID() >= 0, is(true));

            Future<JoinGameResponse> join = connection.sendJoinGame(game.get().getGameID());
            assertThat(join.get(), is(JoinGameResponse.OK));

            Future<Set<String>> players = connection.sendPlayerList(game.get().getGameID());
            assertThat(players.get(), is(new LinkedHashSet<>(Arrays.asList("Nick"))));
        }
    }

    // MOVE

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testMove() throws Exception {
        try (Connection connection = ConnectionProvider.connect()) {
            Future<LogInResponse> response = connection.sendLogIn("Nick");
            assertThat(response.get(), is(LogInResponse.OK));

            Future<NewGameResponse> game = connection.sendNewGame(10, 5);
            assertThat(game.get().getStatus(), is(NewGameResponse.Status.OK));
            assertThat(game.get().getGameID() >= 0, is(true));

            Future<JoinGameResponse> join = connection.sendJoinGame(game.get().getGameID());
            assertThat(join.get(), is(JoinGameResponse.OK));

            List<GameStateResponse.Piece> pieces = Arrays.asList(
                    new GameStateResponse.Piece(0, 45, 95),
                    new GameStateResponse.Piece(1, 120, 64)
            );
            Future<GenericResponse> move = connection.sendGameAction(pieces);
            assertThat(move.get(), is(GenericResponse.OK));
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT)
    public void testWin() throws Exception {
        try (Connection connection = ConnectionProvider.connect()) {
            Future<LogInResponse> response = connection.sendLogIn("Nick");
            assertThat(response.get(), is(LogInResponse.OK));

            Future<NewGameResponse> game = connection.sendNewGame(5, 5);
            assertThat(game.get().getStatus(), is(NewGameResponse.Status.OK));
            assertThat(game.get().getGameID() >= 0, is(true));

            Future<JoinGameResponse> join = connection.sendJoinGame(game.get().getGameID());
            assertThat(join.get(), is(JoinGameResponse.OK));

            CompletableFuture<Void> win = new CompletableFuture<>();
            connection.addConsumer("GWI", MessageConsumer.temporary(r -> {
                win.complete(null);
            }));

            int movX = -594;
            int movY = 846;
            List<GameStateResponse.Piece> pieces = new ArrayList<>();
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    pieces.add(new GameStateResponse.Piece(
                            x + y * 5, movX + x * 100, movY + y * 100));
                }
            }
            Future<GenericResponse> move = connection.sendGameAction(pieces);
            assertThat(move.get(), is(GenericResponse.OK));

            win.get();
        }
    }

}