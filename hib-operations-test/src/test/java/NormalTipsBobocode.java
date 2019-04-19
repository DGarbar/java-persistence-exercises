import com.dgarbar.model.Account;
import javax.persistence.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.procamp.util.EntityManagerUtil;

public class NormalTipsBobocode {
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

    /**
     * Stores and account, then updates its email and tries to store the other account with the previous email value
     * in the scope of the same transaction. It does not work because Hibernate operations are sorted according to
     * its priorities.
     * E.g. INSERT is done before DELETE
     */
    @Test
    void saveAccountWithSameEmailAfterEmailUpdate() {
        Account account = new Account();
        account.setName("a");
        Account secondAccount = new Account();
        secondAccount.setName(account.getName());

        util.performWithinTx(entityManager -> {
            entityManager.persist(account);
            account.setName("UPDATED"); // change the email of the first account
            // won't work because insert will be performed before update
            entityManager.persist(secondAccount); // store second account with the same email
        });
    }

}
