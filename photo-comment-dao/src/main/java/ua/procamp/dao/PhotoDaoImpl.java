package ua.procamp.dao;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.persistence.*;
import org.hibernate.annotations.QueryHints;
import ua.procamp.model.Photo;
import ua.procamp.model.PhotoComment;

/**
 * Please note that you should not use auto-commit mode for your implementation.
 */
public class PhotoDaoImpl implements PhotoDao {

    private EntityManagerFactory entityManagerFactory;

    public PhotoDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Photo photo) {
        performInTransaction(entityManager -> {
            entityManager.persist(photo);
        });
    }

    @Override
    public Photo findById(long id) {
        return performInTransactionWithReturn(entityManager ->
            //Do I need to set readOnly in find ?
            entityManager.find(Photo.class, id)
        );
    }

    @Override
    public List<Photo> findAll() {
        return performInTransactionWithReturn(entityManager ->
            entityManager.createQuery("select p from Photo p")
                .setHint(QueryHints.READ_ONLY, true)
                .getResultList());
    }

    @Override
    public void remove(Photo photo) {
        performInTransaction(entityManager -> {
            Photo mergePhoto = entityManager.merge(photo);
            entityManager.remove(mergePhoto);
        });
    }

    @Override
    public void addComment(long photoId, String commentString) {
        performInTransaction(entityManager -> {
            Photo photo = entityManager.getReference(Photo.class, photoId);
            PhotoComment photoComment = new PhotoComment();
            photoComment.setText(commentString);
            photo.addComment(photoComment);
        });

    }

    private void performInTransaction(Consumer<EntityManager> operations) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            operations.accept(entityManager);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    private <T> T performInTransactionWithReturn(Function<EntityManager, T> operations) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        T result = null;
        try {
            transaction.begin();
            result = operations.apply(entityManager);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return result;
    }
}
