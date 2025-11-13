package fr.upec.episen.paas.cacheloader.repository;

import fr.upec.episen.paas.cacheloader.model.People;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeopleRepository extends JpaRepository<People, Long> {

    @Query("SELECT p FROM People p WHERE CURRENT_TIMESTAMP BETWEEN p.allowedIntervalStart AND p.allowedIntervalEnd")
    List<People> findAllAllowedNow();

    @Query("SELECT p FROM People p WHERE CURRENT_TIMESTAMP NOT BETWEEN p.allowedIntervalStart AND p.allowedIntervalEnd")
    List<People> findAllNotAllowedNow();
}
