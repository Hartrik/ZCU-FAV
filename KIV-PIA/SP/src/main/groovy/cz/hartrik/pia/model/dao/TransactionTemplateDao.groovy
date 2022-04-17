package cz.hartrik.pia.model.dao

import cz.hartrik.pia.model.TransactionTemplate
import cz.hartrik.pia.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Transaction template data access object.
 *
 * @version 2018-12-22
 * @author Patrik Harag
 */
@Repository
interface TransactionTemplateDao extends JpaRepository<TransactionTemplate, Integer> {

    /**
     * Returns all templates created by given user.
     *
     * @param owner user
     * @return templates
     */
    @Query("SELECT t FROM TransactionTemplate t WHERE t.owner = :owner ORDER BY t.name")
    List<TransactionTemplate> findAllByOwner(@Param("owner") User owner)

}
