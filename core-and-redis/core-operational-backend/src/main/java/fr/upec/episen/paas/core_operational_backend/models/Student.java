package fr.upec.episen.paas.core_operational_backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private String num;
    private String firstname;
    private String lastname;
    private String isAuthorized;
}
