package fr.upec.episen.paas.core_operational_backend.dto;

import java.sql.Timestamp;

import fr.upec.episen.paas.core_operational_backend.models.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private Timestamp timestamp;
    private String className;
    private boolean isAllowed;
    private String status;
    private String service = "core-operational-backend";

    public StudentDTO(Long id, String firstname, String lastname, Timestamp timestamp, String className, boolean isAllowed, String status) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.timestamp = timestamp;
        this.className = className;
        this.isAllowed = isAllowed;
        this.status = status;
    }
}
