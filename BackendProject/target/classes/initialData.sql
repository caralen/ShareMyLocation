INSERT INTO korisnik (korisnickoIme, lozinka, spol, ime, prezime, brTelefona, ikona, token)
	values ('NR', 'nr123', 'Ž', 'Nika', 'Rosandić', '0991234567', 'base64encoded', 'dummyToken' );
INSERT INTO korisnik (korisnickoIme, lozinka, spol, ime, prezime, brTelefona, ikona, token)
	values ('LR', 'lr123', 'Ž', 'Lea', 'Rački', '0992345678', 'base64encoded', 'dummyToken' );
INSERT INTO korisnik (korisnickoIme, lozinka, spol, ime, prezime, brTelefona, ikona, token)
	values ('AC', 'ac123', 'M', 'Alen', 'Carin', '0993456789', 'base64encoded', 'dummyToken' );
INSERT INTO korisnik (korisnickoIme, lozinka, spol, ime, prezime, brTelefona, ikona, token)
	values ('AM', 'am123', 'M', 'Antun', 'Modrušan', '0994567890', 'base64encoded', 'dummyToken' );


INSERT INTO grupa (imeGrupa, adminGrupa, brClanova, ikona)
values ('Projekt', 1, 4, 'base64encoded');
INSERT INTO grupa (imeGrupa, adminGrupa, brClanova, ikona)
values ('Družina', 2, 4, 'base64encoded');


INSERT INTO korisnikGrupa (grupaID, korisnikID)
values (2, 1);
INSERT INTO korisnikGrupa (grupaID, korisnikID)
values (2, 2);
INSERT INTO korisnikGrupa (grupaID, korisnikID)
values (2, 4);
INSERT INTO korisnikGrupa (grupaID, korisnikID)
values (2, 3);
INSERT INTO korisnikGrupa (grupaID, korisnikID)
values (1, 1);
INSERT INTO korisnikGrupa (grupaID, korisnikID)
values (1, 2);
INSERT INTO korisnikGrupa (grupaID, korisnikID)
values (1, 3);
INSERT INTO korisnikGrupa (grupaID, korisnikID)
values (1, 4);


INSERT INTO poruka (grupaID, korisnikID, vrijemePoruke, geogSirina, geogDuzina, opis, pozReakcija, negReakcija)
values (1, 4, to_timestamp('21-07-2017 08:15:00', 'dd-mm-yyyy hh24:mi:ss'), 45.2345, 22.1823, 'Jel netko u gradu za kavu?', 1, 0);

INSERT INTO poruka (grupaID, korisnikID, vrijemePoruke, geogSirina, geogDuzina, opis, pozReakcija, negReakcija)
values (2, 2, to_timestamp('05-06-2017 08:15:00', 'dd-mm-yyyy hh24:mi:ss'), 45.2345, 22.1823, 'Netko menza?', 1, 0);

