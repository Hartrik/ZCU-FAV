package cz.hartrik.pia.config

import org.hibernate.c3p0.internal.C3P0ConnectionProvider
import org.hibernate.dialect.Dialect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * Persistence configuration.
 *
 * @version 2018-11-22
 * @author Patrik Harag
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories('cz.hartrik.pia.model.dao')
class PersistenceConfig {

    private static Properties initProperties(Dialect dialect) {
        def properties = new Properties()

        // basic properties
        properties.'hibernate.hbm2ddl.auto' = 'update'
        properties.'hibernate.dialect' = dialect.class.name
        properties.'hibernate.validator.apply_to_ddl' = 'true'

        // connection pool
        properties.'connection.provider_class' = C3P0ConnectionProvider.name
        properties.'hibernate.c3p0.min_size' = '5'
        properties.'hibernate.c3p0.max_size' = '20'
        properties.'hibernate.c3p0.timeout' = '300'
        properties.'hibernate.c3p0.max_statements' = '50'
        properties.'hibernate.c3p0.idle_test_period' = '300'

        return properties
    }

    @Bean
    PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager()
        txManager.setEntityManagerFactory(entityManagerFactory)
        return txManager
    }

    @Bean
    @Autowired
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Dialect dialect) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter()
        vendorAdapter.setGenerateDdl(true)

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean()
        factory.setJpaVendorAdapter(vendorAdapter)
        factory.setJpaProperties(initProperties(dialect))
        factory.setPackagesToScan('cz.hartrik.pia.model')
        factory.setDataSource(dataSource)
        return factory
    }

}
