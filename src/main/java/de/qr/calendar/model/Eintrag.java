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

    private Integer nummer;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "eintrag")
    private Artikel artikel;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "eintrag")
    private Song song;
}
