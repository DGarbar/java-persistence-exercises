import Entities.Account;
import java.util.List;
import javax.persistence.*;
import ua.procamp.util.EntityManagerUtil;

public class testRel {

    public static void main(String[] args) {
        EntityManagerFactory emFactory = Persistence
            .createEntityManagerFactory("TestPersistenceUnit");
        EntityManagerUtil emUtil = new EntityManagerUtil(emFactory);
        emUtil.performWithinTx(entityManager ->
            entityManager.createQuery("Delete from Account")
                .executeUpdate());
        Account accountDetached = emUtil.performReturningWithinTx(entityManager -> {
                Account account = new Account();
                account.setLastName("1");
                entityManager.persist(account);
                return account;
            }
        );
        System.out.println("perform big");
        emUtil.performWithinTx(entityManager -> {
                Account persistedAcc = entityManager.find(Account.class, 1L);
                entityManager.remove(persistedAcc);
                Account newAcc = new Account();
                newAcc.setLastName("asdasd");
                entityManager.persist(newAcc);
                newAcc.setFirstName("asdasdasdasda");

                List select_a_from_account_a = entityManager.createQuery("select a from Account a")
                    .getResultList();
                System.out.println("last line");
            }
        );
        emFactory.close();

    }
}
