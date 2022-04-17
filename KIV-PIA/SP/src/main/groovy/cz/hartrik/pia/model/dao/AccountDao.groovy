package cz.hartrik.pia.model.dao

import cz.hartrik.pia.model.Account
import cz.hartrik.pia.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Account data access object.
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
@Repository
interface AccountDao extends JpaRepository<Account, Integer> {

    /**
     * Returns account by its number.
     *
     * @param accountNumber account number
     * @return account
     */
    Optional<Account> findByAccountNumber(String accountNumber)

    /**
     * Returns all accounts owned by given user.
     *
     * @param owner
     * @return account
     */
    @Query("SELECT a FROM Account a WHERE a.owner = :owner ORDER BY a.id")
    List<Account> findAllByOwner(@Param("owner") User owner)
}
