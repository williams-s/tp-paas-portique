package fr.upec.episen.paas.cacheloader.model;

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

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
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

    public String isShisAuthorizedouldOpen() {
        return isAuthorized;
    }

    public void setIsAuthorized(String isAuthorized) {
        this.isAuthorized = isAuthorized;
    }
}
