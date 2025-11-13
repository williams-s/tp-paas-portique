package fr.upec.episen.paas.core_operational_backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long studentId;
    private Long num;
    private String firstname;
    private String lastname;
    private boolean shouldOpen;

    public Student(People p, boolean shouldOpen) {
        this.num = p.getNum();
        this.firstname = p.getFirstName();
        this.lastname = p.getLastName();
        this.shouldOpen = shouldOpen;
    }
}
