package ua.procamp;

import java.util.function.Function;
import javax.persistence.*;
import org.hibernate.Session;
import ua.procamp.exception.QueryHelperException;

/**
 * {@link QueryHelper} provides a util method that allows to perform read operations in the scope of transaction
 */
public class QueryHelper {
    private EntityManagerFactory entityManagerFactory;

    public QueryHelper(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Receives a {@link Function<EntityManager, T>}, creates {@link EntityManager} instance, starts transaction,
     * performs received function and commits the transaction, in case of exception in rollbacks the transaction and
     * throws a {@link QueryHelperException} with the following message: "Error performing query. Transaction is rolled back"
     * <p>
     * The purpose of this method is to perform read operations using {@link EntityManager}, so it uses read only mode
     * by default.
     *
     * @param entityManagerConsumer query logic encapsulated as function that receives entity manager and returns result
     * @param <T>                   generic type that allows to specify single entity class of some collection
     * @return query result specified by type T
     */
    public <T> T readWithinTx(Function<EntityManager, T> entityManagerConsumer) {
        Session session = entityManagerFactory.createEntityManager().unwrap(Session.class);
        session.setDefaultReadOnly(true);
        EntityTransaction transaction = session.getTransaction();
        try {
            transaction.begin();
            T result = entityManagerConsumer.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw new QueryHelperException("Error performing query. Transaction is rolled back", e);
        } finally {
            session.close();
        }
    }
}
