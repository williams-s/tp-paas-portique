package paas.tp.entrance_cockpit_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import paas.tp.entrance_cockpit_backend.models.People;
import paas.tp.entrance_cockpit_backend.models.Profile;
import paas.tp.entrance_cockpit_backend.repository.PeopleRepository;

@Service
public class PeopleService {


    private PeopleRepository peopleRepository;

    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public Page<People> getPeoplePage(Pageable pageable) {
        return peopleRepository.findAll(pageable);
    }

    public Page<People> search(String firstName, String lastName, String num, Pageable pageable) {return peopleRepository.search(firstName, lastName, num,  pageable);}


}
