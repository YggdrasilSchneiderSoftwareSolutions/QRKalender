package de.qr.calendar.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
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
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EINTRAG")
public class Eintrag {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "kalender_id", nullable = false)
    @NotNull
    private Kalender kalender;

    /** Laufende Nummer des Kalendereintrags */
    private Integer nummer;

    /** Ein Bild, das über dem Text angezeigt wird. Wird für die src-Property des img-Tags verwendet */
    private String bild;

    /** Der Text des Kalendereintrags */
    @Lob
    private String inhalt;

    /** Ein embedded Link z.B. zu einem Song auf Spotify oder Youtube */
    private String link;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate aufrufbarAb;

    @CreationTimestamp
    private LocalDateTime erstellungsdatum;

    @UpdateTimestamp
    private LocalDateTime aenderungsdatum;
}
