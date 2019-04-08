package ua.procamp.dao;

import java.util.List;
import javax.persistence.*;
import ua.procamp.exception.AccountDaoException;
import ua.procamp.model.Account;

public class AccountDaoImpl implements AccountDao {

    private EntityManagerFactory emf;

    public AccountDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void save(Account account) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(account);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new AccountDaoException("Save error", e);
        } finally {
            entityManager.close();
        }

    }

    @Override
    public Account findById(Long id) {
        EntityManager entityManager = emf.createEntityManager();
        Account account;
        try {
            account = entityManager.find(Account.class, id);
        } finally {
            entityManager.close();
        }
        return account;
    }

    @Override
    public Account findByEmail(String email) {
        EntityManager entityManager = emf.createEntityManager();
        Account account;
        try {
            account = (Account) entityManager
                .createQuery("select a from Account a where email = :email")
                .setParameter("email", email)
                .getSingleResult();
        } finally {
            entityManager.close();
        }
        return account;
    }

    @Override
    public List<Account> findAll() {
        EntityManager entityManager = emf.createEntityManager();
        List<Account> accounts;
        try {
            accounts = (List<Account>) entityManager.createQuery("select a from Account a")
                .getResultList();
        } finally {
            entityManager.close();
        }
        return accounts;
    }

    @Override
    public void update(Account account) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(account);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new AccountDaoException("Update error", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void remove(Account account) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Account merge = entityManager.merge(account);
            entityManager.remove(merge);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new AccountDaoException("Remove error", e);
        }finally {
            entityManager.close();
        }
    }
}

