package paas.tp.entrance_cockpit_backend.DTO;

import paas.tp.entrance_cockpit_backend.models.People;

import java.util.List;

public record PeopleDTO(List<People> people, int totalPages, long totalPeople) {
}
