package ua.procamp.dao;

import javax.persistence.*;
import org.hibernate.Session;
import ua.procamp.exception.CompanyDaoException;
import ua.procamp.model.Company;

public class CompanyDaoImpl implements CompanyDao {

    private EntityManagerFactory entityManagerFactory;

    public CompanyDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Company findByIdFetchProducts(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            return entityManager
                .createQuery("select c from Company c left join fetch c.products where c.id = :id ",Company.class)
                .setParameter("id", id)
                .getSingleResult();
        } catch (Exception e) {
            transaction.rollback();
            throw new CompanyDaoException("Error fetching by id ", e);
        } finally {
            entityManager.close();
        }
    }
}
