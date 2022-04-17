package cz.hartrik.pia.service

import cz.hartrik.pia.model.*

/**
 * Authorized template manager.
 *
 * @see TemplateManager
 *
 * @version 2018-12-22
 * @author Patrik Harag
 */
interface AuthorizedTemplateManager {

    /**
     * Creates a new transaction template.
     *
     * @param owner
     * @param name
     * @param amount
     * @param currency
     * @param accountNumber
     * @param bankCode
     * @param description
     * @return template
     */
    TransactionTemplate createTemplate(User owner, String name, BigDecimal amount, Currency currency,
                                       String accountNumber, String bankCode, String description)

    /**
     * Edits a transaction template. Throws exception if not found.
     *
     * @param id
     * @param name
     * @param amount
     * @param currency
     * @param accountNumber
     * @param bankCode
     * @param description
     * @return modified template
     */
    TransactionTemplate editTemplate(Integer id, String name, BigDecimal amount, Currency currency,
                                       String accountNumber, String bankCode, String description)

    /**
     * Returns all templates owned by given user.
     *
     * @param owner user
     * @return templates
     */
    List<TransactionTemplate> findAllTemplatesByOwner(User owner)

    /**
     * Remove template by id. Throws exception if not found.
     *
     * @param id template id
     */
    void remove(Integer id)

    /**
     * Returns template by id. Throws exception if not found.
     *
     * @param id template id
     * @return template
     */
    TransactionTemplate findById(Integer id)

}
