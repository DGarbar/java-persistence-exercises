package ua.procamp;

import java.util.List;
import javax.persistence.*;
import ua.procamp.model.Account;
import ua.procamp.util.TestDataGenerator;

public class EntityManagerExample {

    public static void main(String[] args) {
        //EntityManager is shutdown when application is closed,
        //So close in try-with-resources is not good for closing
        EntityManagerFactory singleAccountEntityH2 = Persistence
            .createEntityManagerFactory("SingleAccountEntityH2");
        //Real Connection. Single NOT thread save session.
        //One session can have multiple transactions.
        EntityManager entityManager = singleAccountEntityH2.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        try {
            Account account = TestDataGenerator.generateAccount();
            entityManager.persist(account);
            entityManager.detach(account);
            //Move to session (to persistent)
            Account merge = entityManager.merge(account);
            Account findAcc = entityManager.find(Account.class, account.getId());

            List<Account> email = entityManager
                .createQuery("select a from Account a where a.email = :email", Account.class)
                .setParameter("email", account.getEmail())
                .getResultList();

            entityManager.remove(account);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            entityManager.close();
        }

        singleAccountEntityH2.close();
    }
}
