package cz.hartrik.pia.service

import cz.hartrik.pia.ObjectNotFoundException
import cz.hartrik.pia.config.*
import cz.hartrik.pia.model.Currency
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
        TemplateManagerImpl
])
class TemplateManagerTest {

    @Autowired
    private TestDatabasePopulatorUsersOnly testData

    @Autowired
    private TemplateManager templateManager

    @Test
    void testCreateEditRemove() {
        templateManager.authorize(testData.user2, {
            def template = createTemplate(testData.user2, "template1", 10, Currency.USD,
                    "6432000001", "1024", "test")
            assert template.name == "template1"
            assert template.amount == 10.toBigDecimal()
            assert template.currency == Currency.USD
            assert template.accountNumber == "6432000001"
            assert template.bankCode == "1024"
            assert template.description == "test"

            def editedUser = editTemplate(template.id, "_template1", 11, Currency.CZK,
                    "6432000002", "1025", "test2")
            assert template.name == "_template1"
            assert template.amount == 11.toBigDecimal()
            assert template.currency == Currency.CZK
            assert template.accountNumber == "6432000002"
            assert template.bankCode == "1025"
            assert template.description == "test2"
            assert editedUser.id == template.id

            remove(template.id)
        })
    }

    @Test(expected = AccessDeniedException)
    void testCreateByAdmin() {
        templateManager.authorize(testData.admin, {
            createTemplate(testData.admin, "template2", null, Currency.USD, null, null, null)
        })
    }

    @Test
    void testCreateAsAdmin() {
        def template = templateManager.authorize(testData.admin, {
            createTemplate(testData.user2, "template3", null, Currency.USD, null, null, null)
        })
        assert template
    }

    @Test(expected = AccessDeniedException)
    void testEditOtherUsersAccount() {
        def template = templateManager.authorize(testData.user2, {
            createTemplate(testData.user2, "template4", null, Currency.USD, null, null, null)
        })

        templateManager.authorize(testData.user1, {
            editTemplate(template.id, "xxx", null, null, null, null, null)
        })
    }

    @Test(expected = ObjectNotFoundException)
    void testRemoveNotFound() {
        templateManager.authorize(testData.admin, {
            remove(4541665)
        })
    }

    @Test(expected = AccessDeniedException)
    void testRemoveOtherUserTemplate() {
        def template = templateManager.authorize(testData.user2, {
            createTemplate(testData.user2, "template5", null, Currency.USD, null, null, null)
        })

        templateManager.authorize(testData.user1, {
            remove(template.id)
        })
    }

    @Test
    void testFindAll() {
        templateManager.authorize(testData.user1, {
            def template = createTemplate(testData.user1, "template6", null, Currency.USD, null, null, null)
            def templates = findAllTemplatesByOwner(testData.user1)

            def t = templates.find { it.name == template.name }
            assert t.currency == Currency.USD
            assert t.amount == null
        })
    }

    @Test(expected = AccessDeniedException)
    void testFindAllOfOtherUser() {
        templateManager.authorize(testData.user1, {
            findAllTemplatesByOwner(testData.user2)
        })
    }

    @Test
    void testFind() {
        def template = templateManager.authorize(testData.user2, {
            createTemplate(testData.user2, "template7", null, Currency.USD, null, null, null)
        })

        templateManager.authorize(testData.admin, {
            def template2 = findById(template.id)
            assert template == template2
        })
    }

    @Test(expected = ObjectNotFoundException)
    void testFindNotFound() {
        templateManager.authorize(testData.user1, {
            findById(845864)
        })
    }

    @Test(expected = AccessDeniedException)
    void testFindOtherUserTemplate() {
        def template = templateManager.authorize(testData.user2, {
            createTemplate(testData.user2, "template8", null, Currency.USD, null, null, null)
        })

        templateManager.authorize(testData.user1, {
            findById(template.id)
        })
    }

}
