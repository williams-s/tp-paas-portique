package paas.tp.entrance_cockpit_backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import paas.tp.entrance_cockpit_backend.DTO.PeopleDTO;
import paas.tp.entrance_cockpit_backend.models.People;
import paas.tp.entrance_cockpit_backend.services.PeopleService;

@RestController
@RequestMapping("/api/people")
public class PeopleController {

    private final PeopleService peopleService;

    private final Logger logger = LogManager.getLogger(PeopleController.class);

    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping("/paginate")
    public PeopleDTO getPeoplePage (@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "id") String sortedBy,
                                    @RequestParam(defaultValue = "asc") String order)
    {
        PageRequest pageRequest;
        try{
            Sort.Direction direction = Sort.Direction.fromString(order);
            pageRequest = PageRequest.of(page, size, Sort.by(direction, sortedBy));
        } catch (Exception e) {
            logger.error("Error parsing page parameters", e);
            return null;
        }
        Page<People> pagePeople = peopleService.getPeoplePage(pageRequest);
        return new PeopleDTO(pagePeople.getContent(), pagePeople.getTotalPages(), pagePeople.getTotalElements());
    }

    @GetMapping("/search")
    public PeopleDTO searchPeople (@RequestParam(defaultValue = "") String criteria,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(defaultValue = "id") String sortedBy,
                                   @RequestParam(defaultValue = "asc") String order)
    {
        PageRequest pageRequest;
        try{
            Sort.Direction direction = Sort.Direction.fromString(order);
            pageRequest = PageRequest.of(page, size, Sort.by(direction, sortedBy));
        } catch (Exception e) {
            logger.error("Error parsing page parameters", e);
            return null;
        }
        Page<People> pagePeople = peopleService.search(criteria.toLowerCase(), pageRequest);
        return new PeopleDTO(pagePeople.getContent(), pagePeople.getTotalPages(), pagePeople.getTotalElements());
    }

}
