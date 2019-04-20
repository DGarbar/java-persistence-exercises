import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dgarbar.model.Account;
import javax.persistence.*;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.procamp.util.EntityManagerUtil;
import ua.procamp.util.TestDataGenerator;

public class persistHibOperation {

    private static EntityManagerUtil util;
       private static EntityManagerFactory singleAccountEntityH2;

    @BeforeAll
    static void init() {
        singleAccountEntityH2 = Persistence
            .createEntityManagerFactory("SingleAccountEntityH2");
        util = new EntityManagerUtil(singleAccountEntityH2);
    }

    @BeforeEach
    void clean() {
        util.performWithinTx(entityManager ->
            entityManager.createNativeQuery("DELETE FROM account"));
    }


    @Test
    void testPersistWithNew() {
        util.performWithinTx(entityManager ->
            entityManager.persist(new Account())
        );
    }


    //Only 1 call to id_seq and 1 Insert
    @Test
    void testPersistWithPersisted() {
        util.performWithinTx(entityManager -> {
            Account entity = new Account();
            entityManager.persist(entity);
            entityManager.persist(entity);
            entityManager.persist(entity);
            entityManager.persist(entity);
        });
    }

    //Nothing saved. Only index is incremented
    @Test
    void testPersistWithPersistedAndAfterDetached() {
        util.performWithinTx(entityManager -> {
            Account entity = new Account();
            entityManager.persist(entity);
            entityManager.persist(entity);
            entityManager.persist(entity);
            entityManager.persist(entity);
            entityManager.detach(entity);
        });
    }

    /**
     * Stores and account, then removes it and tries to store the other account with the save email in the scope of the
     * same transaction. It does not work because Hibernate operations are sorted according to its priorities.
     * E.g. INSERT is done before UPDATE
     */
    @Test
     void saveAccountWithSameEmailAfterRemove() {
        Account account = new Account();
        account.setName("asd");
        util.performWithinTx(entityManager -> entityManager.persist(account));
        Account secondAccount = new Account();
        secondAccount.setName(account.getName());

        util.performWithinTx(entityManager -> {
            Account managedAccount = entityManager.merge(account);
            entityManager.remove(managedAccount); // remove first account from the database
            // won't work because insert will be performed before remove
            entityManager.persist(secondAccount); // store second account with the same email
        });
    }

    //Insert only first bc dirty check is oN
    @Test
    void persistWhenReadOnly() {
        Account acc = util.performReturningWithinTx(entityManager -> {
            Account entity = new Account();
            entity.setName("acc");
            entityManager.persist(entity);
            return entity;
        });

        util.performWithinTx(entityManager -> {
            Session unwrap = entityManager.unwrap(Session.class);
            unwrap.setDefaultReadOnly(true);
            Account merge = (Account) unwrap.merge(acc);
            merge.setName("aaaaaaaaaaaaaaaaaaa");
        });
    }

    //Insert update insert
    @Test
    void persistWhenReadOnly2() {
        EntityManager entityManager = singleAccountEntityH2.createEntityManager();
        Session unwrap = entityManager.unwrap(Session.class);
        EntityTransaction transaction = entityManager.getTransaction();
            unwrap.setDefaultReadOnly(true);
        try {
            transaction.begin();
            unwrap.persist(new Account());
            Account acc = unwrap.byId(Account.class).load(1L);
            acc.setName("asdasd");
            unwrap.persist(new Account());
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Test
    void exceptionWhenPersistWithDetached() {
        // detached entity passed to persist
        assertThrows(PersistenceException.class, () -> util.performWithinTx(entityManager -> {
            Account entity = new Account();
            entityManager.persist(entity);
            entityManager.detach(entity);
            //Id != null
            entityManager.persist(entity);
        }));
    }

}
