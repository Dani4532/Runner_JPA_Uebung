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
        try {
            return entityManager.createQuery("""
                                select runner
                                from Runner runner
                    """).getResultList();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public OptionalDouble getAverageSpeed(Runner runner) {
        try {
            var dataBaseRunner = findById(runner.getId()).get();
            var runs = dataBaseRunner.getRuns();
            if (runs.isEmpty()) {
                return OptionalDouble.empty();
            }

            var query = entityManager.createQuery("""
                        select sum(runs.minutes) / sum(runs.distanceInKm) from Run runs where runs.runner = :runner
                    """);
            query.setParameter("runner", runner);
            var averageSpeed = (Double) query.getSingleResult() / runs.size();

            return OptionalDouble.of(averageSpeed);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Optional<Runner> findById(long id) {
        try {
            return Optional.ofNullable(entityManager.find(Runner.class, id));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Set<Runner> getAllRunnersWithFinishedMarathon() {
        try {
            Set<Runner> runners = new HashSet<>(0);
            var runs = entityManager.createQuery("""
                                select run from Run run where run.distanceInKm >= 42.195
                    """, Run.class).getResultList();
            runs.forEach(run -> runners.add(run.getRunner()));
            return runners;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Runner save(Runner runner) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(runner);
            entityManager.getTransaction().commit();
            return runner;
        } catch (Exception exception) {
            entityManager.getTransaction().rollback();
            throw exception;
        }
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
            return run;
        } catch (Exception exception) {
            entityManager.getTransaction().rollback();
            throw exception;
        }

    }
}
