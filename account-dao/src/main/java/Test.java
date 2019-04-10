import javax.persistence.*;
import ua.procamp.model.Account;
import ua.procamp.model.Card;
import ua.procamp.util.EntityManagerUtil;
import ua.procamp.util.TestDataGenerator;

public class Test {

    public static void main(String[] args) {
        EntityManagerFactory emFactory= Persistence.createEntityManagerFactory("MyPostgres");
        EntityManagerUtil emUtil = new EntityManagerUtil(emFactory);
        Account accountDetached = emUtil.performReturningWithinTx(entityManager -> {
                Account account = TestDataGenerator.generateAccount();
                entityManager.persist(account);
            Card card = new Card();
            card.setName("New Name");
            card.setHolder(account);
            entityManager.persist(card);
            return account;
            }
        );

        emFactory.close();
    }
}
