package de.qr.calendar.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "KALENDER")
public class Kalender {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    private String bezeichnung;

    private String empfaenger;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate gueltigVon;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate gueltigBis;

    @CreationTimestamp
    private LocalDateTime erstellungsdatum;

    @UpdateTimestamp
    private LocalDateTime aenderungsdatum;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "kalender")
    private List<Eintrag> eintraege = new ArrayList<>();
}
