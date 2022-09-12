package de.qr.calendar.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "ARTIKEL")
public class Artikel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private String bild;

    private String inhalt;

    @OneToOne
    @JoinColumn(name = "eintrag_id", referencedColumnName = "id")
    @NotNull
    private Eintrag eintrag;
}
