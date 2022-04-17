package cz.hartrik.pia.service

import cz.hartrik.pia.ObjectNotFoundException
import cz.hartrik.pia.config.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = [
        TestDatabaseConfig, PersistenceConfig, TestSecurityConfig,
        UserNotificationServiceStub, TestDatabasePopulatorUsersOnly,
        UserManagerImpl
])
class UserManagerTest {

    @Autowired
    private TestDatabasePopulatorUsersOnly testData

    @Autowired
    private UserManager userManager

    @Test
    void testFindAll() {
        def users = userManager.authorize(testData.admin, {
            findAllUsers()
        })
        assert users.containsAll([testData.admin, testData.user1, testData.user2])
    }

    @Test(expected = AccessDeniedException)
    void testFindAllAccessDenied() {
        userManager.authorize(testData.user1, {
            findAllUsers()
        })
    }

    @Test
    void testCreateEditRemove() {
        userManager.authorize(testData.admin, {
            def user = create("John", "Doe", "123456798", "example@example.com")
            assert user.firstName == "John"
            assert user.lastName == "Doe"
            assert user.password
            assert user.login

            def editedUser = edit(user.id, "Jonny", user.lastName, user.personalNumber, user.email)
            assert editedUser.firstName == "Jonny"
            assert editedUser.id == user.id

            assert remove(user.id).id == user.id
        })
    }

    @Test(expected = AccessDeniedException)
    void testCreateAccessDenied() {
        userManager.authorize(testData.user1, {
            create("John", "Doe", "123456798", "example@example.com")
        })
    }

    @Test(expected = AccessDeniedException)
    void testEditOtherUsersAccount() {
        userManager.authorize(testData.user1, {
            edit(testData.user2.id, "John", "Doe", "123456798", "example@example.com")
        })
    }

    @Test(expected = ObjectNotFoundException)
    void testRemoveNotFound() {
        userManager.authorize(testData.admin, {
            remove(4541665)
        })
    }

    @Test(expected = AccessDeniedException)
    void testRemoveAccessDenied() {
        userManager.authorize(testData.user1, {
            remove(testData.user2.id)
        })
    }

}
