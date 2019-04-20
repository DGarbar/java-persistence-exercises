import com.dgarbar.model.AdvancedModel.Captain;
import com.dgarbar.model.AdvancedModel.Command;
import com.dgarbar.model.AdvancedModel.Job;
import com.dgarbar.model.AdvancedModel.Member;
import javax.persistence.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.procamp.util.EntityManagerUtil;

public class OrphanRemovalHibOperation {

    private static EntityManagerUtil util;

    @BeforeAll
    static void init() {
        EntityManagerFactory singleAccountEntityH2 = Persistence
            .createEntityManagerFactory("SingleAccountEntityH2");
        util = new EntityManagerUtil(singleAccountEntityH2);
    }

    @BeforeEach
    void clean() {
        util.performWithinTx(entityManager -> {
            entityManager.createNativeQuery("DELETE FROM captain");
            entityManager.createNativeQuery("DELETE FROM member");
            entityManager.createNativeQuery("DELETE FROM command");
        });
    }

    @BeforeEach
    void persistAllWhenParent() {

    }

    @Test
    void removeWhenRemoveThroughMethod() {
        util.performWithinTx(entityManager -> {
            Command command = new Command();
            Captain captain = new Captain();
            Member member = new Member();
            command.addCaptain(captain);
//            command.addMember(member);
            entityManager.persist(command);
            entityManager.persist(captain);
        });
        util.performWithinTx(entityManager -> {
            Captain captain = entityManager.find(Captain.class, 2L);
            Command command = entityManager.find(Command.class, 1L);
            command.removeCaptain(captain);
        });
    }

    @Test
    void removeWhenRemoveThroughCollection() {
        util.performWithinTx(entityManager -> {
            Command command = new Command();
            Captain captain = new Captain();
            Member member = new Member();
            command.addCaptain(captain);
//            command.addMember(member);
            entityManager.persist(command);
            entityManager.persist(captain);
        });
        util.performWithinTx(entityManager -> {
            Captain captain = entityManager.find(Captain.class, 2L);
            Command command = entityManager.find(Command.class, 1L);
            command.getCaptains().remove(captain);
        });
    }

    @Test
    void removeWhenRemoveThroughSetNullOnParent() {
        util.performWithinTx(entityManager -> {
            Command command = new Command();
            Captain captain = new Captain();
            Member member = new Member();
            command.addCaptain(captain);
//            command.addMember(member);
            entityManager.persist(command);
            entityManager.persist(captain);
        });
        util.performWithinTx(entityManager -> {
//            Captain captain = entityManager.find(Captain.class, 2L);
            Command command = entityManager.find(Command.class, 1L);
            command.setCaptains(null);
        });
    }

  @Test
    void removeWhenRemoveFromCollectionButHaveAnotherReferences() {
        util.performWithinTx(entityManager -> {
            Command command = new Command();
            Captain captain = new Captain();
            captain.addJob(new Job());
            Member member = new Member();
            command.addCaptain(captain);
//            command.addMember(member);
            entityManager.persist(command);
            entityManager.persist(captain);
        });
        util.performWithinTx(entityManager -> {
            Captain captain = entityManager.find(Captain.class, 2L);
            Command command = entityManager.find(Command.class, 1L);
            captain.getJobs().forEach(System.out::println);
            command.removeCaptain(captain);
        });
    }


}
