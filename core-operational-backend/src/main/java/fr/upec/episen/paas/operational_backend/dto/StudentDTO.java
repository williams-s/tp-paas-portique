package fr.upec.episen.paas.operational_backend.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private String num;
    private Long doorId;
    private String firstname;
    private String lastname;
    private Timestamp timestamp;
    private String className;
    private boolean allowed;
    private String service = "core-operational-backend";
}
