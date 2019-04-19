import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dgarbar.model.Account;
import javax.persistence.*;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.procamp.util.EntityManagerUtil;

public class UpdateHibOperation {

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


    @Test
    void exceptionWhenPersistWithDetached() {
        assertThrows(PersistenceException.class, () -> util.performWithinTx(entityManager -> {
            Session unwrap = entityManager.unwrap(Session.class);
            unwrap.setDefaultReadOnly(true);
            Account entity = new Account();
            entityManager.persist(entity);
            entityManager.detach(entity);
            //Id != null
            entityManager.persist(entity);
        }));
    }

}
