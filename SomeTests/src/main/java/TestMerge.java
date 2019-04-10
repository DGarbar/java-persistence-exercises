import javax.persistence.*;
import ua.procamp.model.Account;
import ua.procamp.util.EntityManagerUtil;
import ua.procamp.util.TestDataGenerator;

public class TestMerge {

    public static void main(String[] args) {
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("Account");
        EntityManagerUtil emUtil = new EntityManagerUtil(emFactory);
        Account accountDetached = emUtil.performReturningWithinTx(entityManager -> {
                Account account = TestDataGenerator.generateAccount();
                entityManager.persist(account);
                return account;
            }
        );

        emUtil.performWithinTx(entityManager -> {
                entityManager.merge(accountDetached);
            }
        );

        emFactory.close();
    }
}
