-- Datenbank erstellen
CREATE DATABASE IF NOT EXISTS QRKALENDER;

-- Tabellen erstellen

-- Kalender für bestimmte Person mit mehreren Einträgen
CREATE TABLE QRKALENDER.KALENDER
(
    ID          INT NOT NULL UNIQUE AUTO_INCREMENT,
    BEZEICHNUNG VARCHAR(255) NOT NULL,
    EMPFAENGER  VARCHAR(127) NOT NULL,
    PRIMARY KEY (ID)
);

-- Eintrag ist der konkrete Kalendereintrag, bestehend aus verschiedenen Elementen
-- z.B. Artikel, Song, etc.
CREATE TABLE QRKALENDER.EINTRAG
(
    ID          INT NOT NULL UNIQUE AUTO_INCREMENT,
    KALENDER_ID INT NOT NULL,
    NUMMER      INT NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (KALENDER_ID) REFERENCES QRKALENDER.KALENDER (ID)
);

-- Artikel ist der Hauptbestandteil, z.B. Rezept, Gutschein, Ausflug etc.
-- Kann Bild enthalten
CREATE TABLE QRKALENDER.ARTIKEL
(
    ID          INT NOT NULL UNIQUE AUTO_INCREMENT,
    BILD        VARCHAR(255),
    INHALT      VARCHAR(4095) NOT NULL,
    EINTRAG_ID  INT NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (EINTRAG_ID) REFERENCES QRKALENDER.EINTRAG (ID)
);

-- Song des Tages (Spotify-Link, YT etc.)
CREATE TABLE QRKALENDER.SONG 
(
    ID          INT NOT NULL UNIQUE AUTO_INCREMENT,
    LINK        VARCHAR(1023) NOT NULL,
    EINTRAG_ID  INT NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (EINTRAG_ID) REFERENCES QRKALENDER.EINTRAG (ID)
);

-- Feedback, das der Anwender zu einem Eintrag geben kann
-- Kann auch eine Art "Beweis" sein für eine Challenge
CREATE TABLE QRKALENDER.FEEDBACK
(
    ID          INT NOT NULL UNIQUE AUTO_INCREMENT,
    BILD        VARCHAR(255),
    EINTRAG_ID  INT NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (EINTRAG_ID) REFERENCES QRKALENDER.EINTRAG (ID)
);