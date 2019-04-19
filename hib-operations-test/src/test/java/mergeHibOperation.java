import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dgarbar.model.Account;
import javax.persistence.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.procamp.util.EntityManagerUtil;

public class mergeHibOperation {

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
    void mergeDetachedUnchanged() {
        Account asd = util.performReturningWithinTx(entityManager ->
        {
            Account account = new Account();
            account.setName("asd");
            entityManager.persist(account);
            return account;
        });

        util.performWithinTx(entityManager -> {
            entityManager.merge(asd);
        });
    }
    @Test
    void mergeDetachedChanged() {
        Account asd = util.performReturningWithinTx(entityManager ->
        {
            Account account = new Account();
            account.setName("asd");
            entityManager.persist(account);
            return account;
        });
        asd.setName("asdasd");
        util.performWithinTx(entityManager -> {
            entityManager.merge(asd);
        });
    }

    @Test
    void mergePersisted() {
        Account asd = util.performReturningWithinTx(entityManager ->
        {
            Account account = new Account();
            account.setName("asd");
            entityManager.persist(account);
            entityManager.flush();

            Account merge = entityManager.merge(account);
            merge.setName("asdadasda");
            return account;

        });
    }
}
