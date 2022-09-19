package de.qr.calendar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
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

    /** Ein Bild, das über dem Text angezeigt wird */
    private String bild;

    /** Der Text des Kalendereintrags */
    @Lob
    private String inhalt;

    /** Ein embedded Link z.B. zu einem Song auf Spotify oder Youtube */
    private String link;
}
