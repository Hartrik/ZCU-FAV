package cz.hartrik.pia.model

import cz.hartrik.pia.JavaBank
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*

/**
 * Account entity.
 *
 * @version 2018-11-25
 * @author Patrik Harag
 */
@EqualsAndHashCode(excludes = ['owner'])
@ToString(excludes = ['owner'])
@Entity
@Table(name = 'table_account')
class Account implements EntityObject<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    Integer id

    /**
     * Account balance.
     */
    @Column(nullable = false)
    BigDecimal balance

    /**
     * Account owner.
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    User owner

    /**
     * Account number (without bank code).
     */
    @Column(nullable = false, unique = true)
    String accountNumber

    /**
     * Card number.
     */
    @Column(nullable = false, unique = true)
    String cardNumber

    /**
     * Currency.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    Currency currency

    /**
     * Returns full account number.
     *
     * @return account number
     */
    @Transient
    String getAccountNumberFull() {
        return accountNumber + "/" + JavaBank.CODE
    }

}
