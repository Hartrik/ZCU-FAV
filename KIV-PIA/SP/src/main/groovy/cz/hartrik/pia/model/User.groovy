package cz.hartrik.pia.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import javax.persistence.*

/**
 * User entity.
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
@EqualsAndHashCode(excludes = 'accounts')
@ToString(excludes = 'accounts')
@Entity
@Table(name = 'table_user')
class User implements UserDetails, EntityObject<Integer> {

    static final String ROLE_CUSTOMER = 'CUSTOMER'
    static final String ROLE_ADMIN = 'ADMIN'


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    Integer id

    /**
     * User enabled.
     */
    @Column(nullable = false)
    Boolean enabled = true

    /**
     * User role.
     */
    @Column(nullable = false)
    String role

    /**
     * User's first name.
     */
    @Column(nullable = false)
    String firstName

    /**
     * User's last name.
     */
    @Column(nullable = false)
    String lastName

    /**
     * User's personal number.
     */
    @Column(nullable = false)
    String personalNumber

    /**
     * User's email.
     */
    @Column(nullable = false)
    String email

    /**
     * User's login.
     */
    @Column(nullable = false, unique = true)
    String login

    /**
     * User's password (hash).
     */
    @Column(nullable = false)
    String password

    /**
     * Accounts created by the user.
     */
    @OneToMany(mappedBy = 'owner')
    Set<Account> accounts

    // UserDetails

    @Override
    @Transient
    Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role))
    }

    @Override
    @Transient
    String getPassword() {
        return password
    }

    @Override
    @Transient
    String getUsername() {
        return login
    }

    @Override
    @Transient
    boolean isAccountNonExpired() {
        return true
    }

    @Override
    @Transient
    boolean isAccountNonLocked() {
        return true
    }

    @Override
    @Transient
    boolean isCredentialsNonExpired() {
        return true
    }

    @Override
    @Transient
    boolean isEnabled() {
        return enabled
    }

}
