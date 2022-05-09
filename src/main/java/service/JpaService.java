package service;

import domain.Run;
import domain.Runner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.*;

public class JpaService implements Service {

    public EntityManager entityManager;

    public JpaService(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public List<Runner> findAll() {
        return entityManager.createQuery("""
                            select runner
                            from Runner runner
                """).getResultList();
    }

    @Override
    public OptionalDouble getAverageSpeed(Runner runner) {
        var dataBaseRunner = findById(runner.getId()).get();
        var runs = dataBaseRunner.getRuns();
        if (runs.isEmpty()){
            return OptionalDouble.empty();
        }
        double speed = 0;
        for (Run e : runs) {
            speed = e.getDistanceInKm() / e.getMinutes() ;
        }
        double averageSpeed = speed / runs.size();
        return OptionalDouble.of(averageSpeed);
    }

    @Override
    public Optional<Runner> findById(long id) {
        var runner = Optional.ofNullable(entityManager.find(Runner.class, id));
        if(runner.isEmpty()){
            return Optional.empty();
        }
        return runner; // Runs nachladen siehe slides.
    }

    @Override
    public Set<Runner> getAllRunnersWithFinishedMarathon() {
        Set<Runner> runners = new HashSet<>(0);
        var runs = entityManager.createQuery("""
            select run from Run run where run.distanceInKm >= 42.195
""",Run.class).getResultList();
        runs.forEach(run -> runners.add(run.getRunner()));
        return runners;
    }

    @Override
    public Runner save(Runner runner) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(runner);
            entityManager.getTransaction().commit();
        }catch (Exception exception){
            entityManager.getTransaction().rollback();
            throw exception;
        }
        return runner;
    }

    @Override
    public Run save(Run run) {
        try {
            var runner = run.getRunner();
            runner.addRuns(run);
            entityManager.getTransaction().begin();
            entityManager.persist(runner);
            entityManager.persist(run);
            entityManager.getTransaction().commit();

        }catch (Exception exception){
            entityManager.getTransaction().rollback();
            throw exception;
        }
        return run;

    }
}
