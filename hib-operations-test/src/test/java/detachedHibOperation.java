import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dgarbar.model.Account;
import javax.persistence.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.procamp.util.EntityManagerUtil;

public class detachedHibOperation {

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




    //Nothing
    @Test
    void testPersistWithPersistedAndAfterDetached() {
        util.performWithinTx(entityManager -> {
            Account entity = new Account();
            entity.setId(1L);
            entityManager.detach(entity);
        });
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
