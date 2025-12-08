package paas.tp.entrance_cockpit_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import paas.tp.entrance_cockpit_backend.models.People;

public interface PeopleRepository extends JpaRepository<People, Long> {

    @Override
    Page<People> findAll(Pageable pageable);

    @Query("SELECT p FROM People p WHERE (:q IS NULL OR lower(p.firstName) LIKE %:q% OR lower(p.lastName) LIKE %:q% OR lower(p.num) LIKE %:q%)")
    Page<People> search(@Param("q") String query, Pageable pageable);
}
