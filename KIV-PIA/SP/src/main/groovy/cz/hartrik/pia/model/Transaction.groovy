package cz.hartrik.pia.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Transaction entity.
 *
 * @version 2018-11-24
 * @author Patrik Harag
 */
@EqualsAndHashCode
@ToString
@Entity
@Table(name = 'table_transaction')
class Transaction implements EntityObject<Integer> {

    private static def ISO_8601_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    Integer id

    /**
     * The amount sent in sender's currency.
     */
    BigDecimal amountSent

    /**
     * The amount received in receiver's currency.
     */
    @Column(nullable = false)
    BigDecimal amountReceived

    /**
     * Sender's account reference (only for local accounts).
     */
    @ManyToOne
    Account sender

    /**
     * Sender's account number.
     */
    @Column(nullable = false)
    String senderAccountNumber

    /**
     * Receiver's account reference (only for local accounts).
     */
    @ManyToOne
    Account receiver

    /**
     * Receiver's account number.
     */
    @Column(nullable = false)
    String receiverAccountNumber

    /**
     * Date when transaction has been performed.
     */
    @Basic
    @Column(nullable = false)
    ZonedDateTime date

    /**
     * Returns date formatted as ISO 8601.
     *
     * @return formatted date
     */
    @Transient
    String getDateAsIso8601() {
        date.format(ISO_8601_FORMATTER)
    }

    /**
     * Description/node.
     */
    String description

}
