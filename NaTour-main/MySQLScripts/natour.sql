CREATE TABLE Utente(
	name varchar(40) Not null,
  	username varchar(40) Not null,
  	surname varchar(40) Not null,
  	email varchar(60) Not null primary key
);

CREATE TABLE Compilation (
	id  int auto_increment primary key,
  	nome varchar(50) not null,
    emailUtente varchar(60) not null,
  	FOREIGN KEY (emailUtente) REFERENCES Utente(email) on DELETE CASCADE,
  	UNIQUE (nome, emailUtente)
);

CREATE TABLE Sentiero(
	id int auto_increment primary key,
  	durata int Not null,
  	inizio_latitudine double Not null,
  	inizio_longitudine DOuble Not null,
  	fine_latitudine Double Not null,
  	fine_longitudine Double Not null,
  	percorso_geografico json Not null,
  	difficolta enum('LOW','AVERAGE','HIGH','EXPERT') Not null,
  	descrizione varchar(1000),
  	emailUtente varchar(60),
  	nome varchar(40) Not null,
  	accessibilitaDisabili Bool Not null,
  	region varchar(60) Not null,
  	country varchar(60) Not null,
  	UNIQUE (emailUtente, nome),
  	FOREIGN KEY (emailUtente) REFERENCES Utente(email) on DELETE CASCADE
);

create table CompilationSentiero(
		idCompilation int,
        idSentiero int,
        primary key (idCompilation, idSentiero),
        foreign key (idCompilation) references Compilation(id) on DELETE CASCADE,
        foreign key (idSentiero) references Sentiero(id) on DELETE CASCADE
);

CREATE TABLE Photo (
  id int auto_increment primary key,
  s3_key varchar(40) not null,
  idSentiero int,
  emailUtente varchar(60),
  FOREIGN KEY (idSentiero) REFERENCES Sentiero(id) on DELETE CASCADE, 
  FOREIGN KEY (emailUtente) REFERENCES Utente(email) on DELETE CASCADE
);
  
CREATE TABLE Punto_di_interesse(
	latitudine DOUBLE not null,
  	longitudine DOUBLE not null,
  	tipologia enum('GROTTA','SORGENTE','PUNTO_PANORAMICO','AREA_PIC_NIC','BAITA','FLORA','LUOGO_DI_INTERESSE_ARTISTICO','ALTRO') not null,
  	nome varchar(40) not null,
  	emailUtente varchar(60),
  	idSentiero int,
  	PRIMARY KEY (nome, emailUtente, idSentiero),
  	FOREIGN KEY (emailUtente) REFERENCES Utente(email) on DELETE CASCADE,
  	FOREIGN KEY (idSentiero) REFERENCES Sentiero(id) on DELETE CASCADE
);
  
create table Recensione(
	id  int auto_increment primary key,
    descrizione varchar(300),
    emailUtente varchar(60),
    idSentiero int,
    valutazione int, check (valutazione < 6 AND valutazione > 0),
	foreign key (idSentiero) references Sentiero(id) on DELETE CASCADE,
    foreign key (emailUtente) references Utente(email) on DELETE CASCADE
);

