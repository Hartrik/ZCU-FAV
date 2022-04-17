package cz.hartrik.pia.service

import org.springframework.data.util.Pair
import org.springframework.stereotype.Service

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

/**
 *
 * @version 2018-11-25
 * @author Patrik Harag
 */
@Service
class TuringTestServiceImpl implements TuringTestService {

    private final Random random = new Random()
    private final List<Pair<String, Predicate<String>>> tests = []
    private final Map<String, TuringTest> activeTests = new ConcurrentHashMap<>()

    @Override
    synchronized void register(String question, Predicate<String> predicate) {
        tests.add(Pair.of(question, predicate))
    }

    @Override
    synchronized TuringTest randomTest() {
        String id = UUID.randomUUID().toString()
        def pair = tests[random.nextInt(tests.size())]
        def test = new TuringTest() {
            @Override
            String getId() {
                return id
            }

            @Override
            String getQuestion() {
                return pair.first
            }

            @Override
            boolean test(String response) {
                return pair.second.test(response)
            }
        }
        activeTests.put(id, test)
        return test
    }

    @Override
    synchronized Boolean testAnswer(String id, String answer) {
        def test = activeTests.get(id)
        if (test == null) {
            return null
        }

        if (test.test(answer)) {
            activeTests.remove(id)
            return true
        }
        return false
    }

}
