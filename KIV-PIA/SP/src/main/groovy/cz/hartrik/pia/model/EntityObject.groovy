package cz.hartrik.pia.model

/**
 * Base interface for all model objects.
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
interface EntityObject<PK extends Serializable> {

    /**
     * Object id.
     *
     * @return  primary key of the instance
     */
    PK getId()

}
