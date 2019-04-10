import java.util.List;
import javax.persistence.*;
import ua.procamp.model.Account;
import ua.procamp.model.Cart;
import ua.procamp.util.EntityManagerUtil;
import ua.procamp.util.TestDataGenerator;

public class TestFlush {

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
            Cart cart = new Cart();
            cart.setFirstName("asd");
            entityManager.persist(cart);
            List<Account> list = entityManager
                .createQuery("select a from Account a", Account.class).getResultList();
            System.out.println("LAST");
        });

        emFactory.close();
    }
}

