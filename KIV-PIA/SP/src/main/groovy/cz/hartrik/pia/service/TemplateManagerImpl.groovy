package cz.hartrik.pia.service

import cz.hartrik.pia.ObjectNotFoundException
import cz.hartrik.pia.model.*
import cz.hartrik.pia.model.dao.TransactionTemplateDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.util.function.Supplier

/**
 * Template manager implementation.
 *
 * @version 2018-12-22
 * @author Patrik Harag
 */
@Transactional
@Service
class TemplateManagerImpl implements TemplateManager {

    private static final Supplier TEMPLATE_NOT_FOUND = {
        new ObjectNotFoundException("Template not found!")
    }


    @Autowired
    TransactionTemplateDao transactionTemplateDao

    @Override
    <T> T authorize(User user, @DelegatesTo(AuthorizedTemplateManager) Closure<T> transaction) {
        transaction.delegate = new AuthorizedTemplateManagerImpl(user)
        transaction()
    }

    private class AuthorizedTemplateManagerImpl implements AuthorizedTemplateManager {

        private final User currentUser

        AuthorizedTemplateManagerImpl(User currentUser) {
            this.currentUser = currentUser
        }

        @Override
        TransactionTemplate createTemplate(User owner, String name, BigDecimal amount,
                                           Currency currency, String accountNumber, String bankCode, String description) {

            if (owner.role == User.ROLE_ADMIN) {
                throw new AccessDeniedException("Admin cannot have an account")
            }
            if (currentUser.role != User.ROLE_ADMIN && owner.id != currentUser.id) {
                throw new AccessDeniedException("Cannot create template owned by another user")
            }

            transactionTemplateDao.save(new TransactionTemplate(
                    owner: owner,
                    name: name,
                    amount: amount,
                    currency: currency,
                    accountNumber: accountNumber,
                    bankCode: bankCode,
                    description: description
            ))
        }

        @Override
        TransactionTemplate editTemplate(Integer id, String name, BigDecimal amount, Currency currency,
                String accountNumber, String bankCode, String description) {

            def template = transactionTemplateDao.findById(id).orElseThrow(TEMPLATE_NOT_FOUND)
            if (currentUser.role != User.ROLE_ADMIN && template.owner.id != currentUser.id) {
                throw new AccessDeniedException("Cannot edit other user's templates")
            }

            transactionTemplateDao.save(new TransactionTemplate(
                    id: template.id,
                    owner: template.owner,
                    name: name,
                    amount: amount,
                    currency: currency,
                    accountNumber: accountNumber,
                    bankCode: bankCode,
                    description: description
            ))
        }

        @Override
        List<TransactionTemplate> findAllTemplatesByOwner(User owner) {
            if (currentUser.role != User.ROLE_ADMIN && owner.id != currentUser.id) {
                throw new AccessDeniedException("Cannot show other user's templates")
            }

            transactionTemplateDao.findAllByOwner(owner)
        }

        @Override
        void remove(Integer id) {
            def template = transactionTemplateDao.findById(id).orElseThrow(TEMPLATE_NOT_FOUND)
            if (currentUser.role != User.ROLE_ADMIN && template.owner.id != currentUser.id) {
                throw new AccessDeniedException("Cannot remove other user's templates")
            }

            transactionTemplateDao.delete(template)
        }

        @Override
        TransactionTemplate findById(Integer id) {
            def template = transactionTemplateDao.findById(id).orElseThrow(TEMPLATE_NOT_FOUND)
            if (currentUser.role != User.ROLE_ADMIN && template.owner.id != currentUser.id) {
                throw new AccessDeniedException("Cannot show other user's templates")
            }

            template
        }
    }

}
