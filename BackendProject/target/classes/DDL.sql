CREATE TABLE korisnik(
    korisnikID SERIAL PRIMARY KEY,
    korisnickoIme VARCHAR(20),
    lozinka VARCHAR(100) NOT NULL,
    spol CHAR(1) NOT NULL,
    ime VARCHAR(20) NOT NULL,
    prezime VARCHAR(20) NOT NULL,
    brTelefona VARCHAR(20) NOT NULL,
    ikona VARCHAR(10485760),
    token VARCHAR(4096) NOT NULL,
    CONSTRAINT uniqueKorisnik UNIQUE (korisnickoIme)
    );

CREATE TABLE grupa(
    grupaID SERIAL PRIMARY KEY,
    imeGrupa VARCHAR(20), -- Ili imeGrupe kako je to Alen nazvao
    adminGrupa INTEGER NOT NULL,
    brClanova SMALLINT NOT NULL,
    ikona VARCHAR (10485760),
    CONSTRAINT uniqueGrupa UNIQUE (imeGrupa),
    CONSTRAINT fkAdminGrupa FOREIGN KEY(adminGrupa) REFERENCES korisnik(korisnikID)
	ON UPDATE CASCADE
);

CREATE TABLE poruka(
  porukaID SERIAL PRIMARY KEY,
  grupaID INTEGER NOT NULL,
  korisnikID INTEGER NOT NULL,
  vrijemePoruke TIMESTAMP,
  geogSirina DECIMAL(16, 13) NOT NULL,
  geogDuzina DECIMAL(16, 13 ) NOT NULL,
  opis VARCHAR(50) NOT NULL,
  pozReakcija SMALLINT NOT NULL,
  negReakcija SMALLINT NOT NULL,
  CONSTRAINT fkGrupaId FOREIGN KEY (grupaID) references grupa(grupaID)
  ON UPDATE CASCADE
  ON DELETE CASCADE,
  CONSTRAINT fkKorisnikID FOREIGN KEY (korisnikID) references korisnik(korisnikID)
);



CREATE TABLE korisnikGrupa(
    korisnikGrupa SERIAL PRIMARY KEY,
    grupaID INTEGER NOT NULL,
    korisnikID INTEGER NOT NULL,
    CONSTRAINT fkKUGKorisnikGrupa FOREIGN KEY (grupaID) REFERENCES grupa(grupaID)
	ON UPDATE CASCADE
	ON DELETE CASCADE,
    CONSTRAINT fkKUGkorisnikID FOREIGN KEY (korisnikID) REFERENCES korisnik(korisnikID)
    );

ALTER TABLE korisnik ADD COLUMN email varchar(50) DEFAULT 'dummy@email' NOT NULL;