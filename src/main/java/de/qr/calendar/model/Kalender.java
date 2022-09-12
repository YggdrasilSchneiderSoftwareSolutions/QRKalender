package de.qr.calendar.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "KALENDER")
public class Kalender {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private String bezeichnung;

    private String empfaenger;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "kalender")
    private List<Eintrag> eintraege;
}
