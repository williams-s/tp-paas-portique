package paas.tp.entrance_cockpit_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import paas.tp.entrance_cockpit_backend.models.People;
import paas.tp.entrance_cockpit_backend.models.Profile;

public interface PeopleRepository extends JpaRepository<People, Long> {

    @Override
    Page<People> findAll(Pageable pageable);

    @Query("Select p from People p where (:firstName is null or p.firstName like %:firstName%) and (:lastName is null or p.lastName like %:lastName%) and (:num is null or p.num like %:num%)")
    Page<People> search(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("num") String num, Pageable pageable);
}
