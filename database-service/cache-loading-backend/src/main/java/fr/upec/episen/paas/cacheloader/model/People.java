package fr.upec.episen.paas.cacheloader.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime; // Utilisation de OffsetDateTime pour TIMESTAMP WITH TIME ZONE

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
    private OffsetDateTime allowedIntervalStart;

    @Column(name = "allowed_interval_end")
    private OffsetDateTime allowedIntervalEnd;

    @Column(name = "registration_date")
    private OffsetDateTime registrationDate;
}