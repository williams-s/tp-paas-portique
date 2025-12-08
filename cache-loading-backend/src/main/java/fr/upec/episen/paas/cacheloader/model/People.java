package fr.upec.episen.paas.cacheloader.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity // Indique que cette classe est une entité JPA
@Table(name = "people") // Mappe à la table 'registered_people' dans la DB
@Data // Génère getters, setters, toString, equals, hashCode (Lombok)
@NoArgsConstructor // Génère un constructeur sans arguments (Lombok)
@AllArgsConstructor // Génère un constructeur avec tous les arguments (Lombok)
public class People {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentation pour l'ID
    private Long id;

    @Column(name = "num", unique = true)
    private String num;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "function", nullable = false)
    private Profile function;

    @Column(name = "allowed_interval_start")
    private LocalDateTime allowedIntervalStart;

    @Column(name = "allowed_interval_end")
    private LocalDateTime allowedIntervalEnd;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Profile getFunction() {
        return function;
    }

    public void setFunction(Profile function) {
        this.function = function;
    }

    public LocalDateTime getAllowedIntervalStart() {
        return allowedIntervalStart;
    }

    public void setAllowedIntervalStart(LocalDateTime allowedIntervalStart) {
        this.allowedIntervalStart = allowedIntervalStart;
    }

    public LocalDateTime getAllowedIntervalEnd() {
        return allowedIntervalEnd;
    }

    public void setAllowedIntervalEnd(LocalDateTime allowedIntervalEnd) {
        this.allowedIntervalEnd = allowedIntervalEnd;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}