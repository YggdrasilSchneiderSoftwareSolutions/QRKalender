package de.qr.calendar.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "EINTRAG")
public class Eintrag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kalender_id", nullable = false)
    @NotNull
    private Kalender kalender;

    /** Laufende Nummer des Kalendereintrags */
    private Integer nummer;

    /** Ein Bild, das über dem Text angezeigt wird */
    private String bild;

    /** Der Text des Kalendereintrags */
    private String inhalt;

    /** Ein embedded Link z.B. zu einem Song auf Spotify oder Youtube */
    private String link;
}
