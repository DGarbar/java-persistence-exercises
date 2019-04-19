import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dgarbar.model.Account;
import javax.persistence.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.procamp.util.EntityManagerUtil;

public class removeHibOperation {
    private static EntityManagerUtil util;

    @BeforeAll
    static void init() {
        EntityManagerFactory singleAccountEntityH2 = Persistence
            .createEntityManagerFactory("SingleAccountEntityH2");
        util = new EntityManagerUtil(singleAccountEntityH2);
    }

    @BeforeEach
    void clean() {
        util.performWithinTx(entityManager ->
            entityManager.createNativeQuery("DELETE FROM account"));
    }

    //Insert and than delete. Bc flush operation ordering.
    @Test
    void testRemoveWithPersisted() {
        util.performWithinTx(entityManager -> {
            Account entity = new Account();
            entityManager.persist(entity);
            entityManager.remove(entity);
        });
    }

    //Insert select remove
    @Test
    void testRemoveAfterMerge() {
        util.performWithinTx(entityManager -> {
            Account entity = new Account();
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();

            Account merge = entityManager.merge(entity);
            entityManager.remove(merge);
        });
    }

    //Insert Delete
    @Test
    void removeAfterMergeWhenInSession() {
        util.performWithinTx(entityManager -> {
            Account entity = new Account();
            entityManager.persist(entity);
            //ignored
            Account merge = entityManager.merge(entity);
            entityManager.remove(merge);
        });
    }

    //Nothing
    @Test
    void testRemoveWithTransient() {
        util.performWithinTx(entityManager -> {
            Account entity = new Account();
            entityManager.remove(entity);
        });
    }

    @Test
    void exceptionWhenRemoveWithDetached() {
        assertThrows(IllegalArgumentException.class, () -> util.performWithinTx(entityManager -> {
            Account entity = new Account();
            entity.setId(1L);
            entityManager.remove(entity);
        }));
    }




}
