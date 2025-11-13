package fr.upec.episen.paas.cacheloader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long num;
    private String firstname;
    private String lastname;
    private boolean shouldOpen;

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isShouldOpen() {
        return shouldOpen;
    }

    public void setShouldOpen(boolean shouldOpen) {
        this.shouldOpen = shouldOpen;
    }
}
