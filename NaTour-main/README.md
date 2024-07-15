# NaTour
## A Modern Social Platform for excursions' lovers.

- ### NaTour21 è un sistema complesso e distribuito finalizzato ad offrire un moderno social network multipiattaforma per appassionati di escursioni. 
Il sistema consiste in un back-end sicuro, performante e scalabile, e in un client mobile attraverso cui gli 
utenti possono fruire delle funzionalità del sistema in modo intuitivo, rapido e piacevole. 
Le principali funzionalità offerte da NaTour21 sono indicate di seguito:
1. Un utente può registrarsi\autenticarsi. È apprezzata la possibilità di autenticarsi utilizzando account 
su altre piattaforme come Google o Facebook.
2. Un utente autenticato può inserire nuovi itinerari (sentieri) in piattaforma. Un sentiero è 
caratterizzato da un nome, una durata, un livello di difficoltà, un punto di inizio, una descrizione 
(opzionale), e un tracciato geografico (opzionale) che lo rappresenta su una mappa. Il tracciato 
geografico deve essere inseribile manualmente (interagendo con una mappa interattiva) oppure 
ramite file in formato standard GPX.
3. Un utente può inserire punti di interesse escursionistico/naturalistico pertinenti un certo itinerario. 
Questi punti sono caratterizzati da una tipologia (e.g.: sorgente, punto panoramico, area pic-nic, 
baita, flora, grotte, luoghi di interesse artistico, altro), e una posizione geografica, e vengono mostrati 
sulla mappa nel dettaglio del sentiero. 
4. Un utente può inserire un tracciato geografico per un sentiero anche registrando una sequenza di 
posizioni GPS con il proprio dispositivo mobile, all’interno dell’app NaTour.
5. Effettuare ricerche di itinerari tra quelli presenti in piattaforma, con possibilità di filtrare i risultati 
per area geografica, per livello di difficoltà, per durata, e per accessibilità a disabili.
6. Visualizzare una schermata di dettaglio per ciascun sentiero. Questa schermata mostra tutte le 
informazioni note del sentiero, e visualizza su una mappa, preferibilmente interattiva, il punto di 
inizio e il tracciato geografico, se disponibile. Inoltre (si vedano funzionalità successive), la schermata 
di dettaglio mostra le eventuali recensioni degli utenti e fotografie caricate.
7. L’utente, tramite una apposita spunta, può mostrare anche la propria posizione (se il dispositivo 
mobile supporta localizzazione GPS) sulla mappa visualizzata nella schermata di dettaglio di un 
itinerario.
8. Un utente può scaricare le informazioni riguardo un sentiero in formato GPX, e stampare le 
informazioni riepilogative in formato PDF.
9. Un utente può inserire una recensione per uno dei sentieri. La recensione consiste in un punteggio 
su una scala fissa, e in una descrizione testuale. 
10. Qualora lo ritenga necessario, un utente può anche indicare un punteggio di difficoltà e/o un tempo 
di percorrenza diverso da quello indicato dall’utente che ha inserito il sentiero. In questo caso, il 
punteggio di difficoltà e il tempo di percorrenza per il sentiero saranno ri-calcolati come la media 
delle difficoltà / dei tempi indicati.
11. Un utente può aggiungere o rimuovere sentieri dalla propria lista di preferiti, o di sentieri da visitare;
12. Un utente può creare compilation di sentieri personalizzate, caratterizzate anche da un titolo e da 
una descrizione personalizzata.
13. Un utente può caricare delle fotografie scattate percorrendo un sentiero. Le fotografie 
corrispondenti a un sentiero vengono mostrate nella pagina di dettaglio di quel sentiero. Inoltre, se la fotografia ha una posizione geografica di scatto salvata nei metadati, è apprezzata la possibilità di 
visualizzare un marker corrispondente alla fotografia sulla mappa, per mostrare in quale punto del 
sentiero è stata scattata. L’introduzione di un sistema in grado di riconoscere automaticamente (e 
bloccare) eventuali immagini inappropriate/offensive è facoltativa ma estremamente apprezzata.
14. Un utente può visualizzare il profilo di un utente, che riporta le sue attività più recenti (e.g.: itinerari
inseriti, compilation create, recensioni/fotografie caricate).
15. Un utente può inviare un messaggio privato (PM) a un altro utente, per esempio per chiedere 
ulteriori informazioni circa un itinerario da lui inserito. È possibile rispondere ai messaggi privati 
ricevuti.
16. Un utente può segnalare informazioni inesatte/non aggiornate riguardo un sentiero. Una 
segnalazione è caratterizzata da un titolo e da una descrizione. I sentieri per cui sono presenti 
segnalazioni di inesattezza mostrano un warning nella schermata di dettaglio relativa.
17. Un utente può segnalare fotografie inappropriate. Le fotografie con segnalazioni non gestite non 
vengono mostrate nella schermata di dettaglio di un sentiero.
18. Quando viene inserita una segnalazione circa un sentiero, l’utente che ha creato il sentiero riceve 
una notifica e ha possibilità di rispondere, utilizzando un campo di testo.
19. Gli amministratori possono visualizzare l’elenco dei sentieri che hanno ricevuto segnalazioni e, per 
ciascuna segnalazione, visualizzare la motivazione e l’eventuale risposta dell’utente che ha caricato 
il sentiero. Gli amministratori possono quindi gestire la segnalazione decidendo se eliminare il 
sentiero oppure mantenerlo.
20. Gli amministratori possono visualizzare l’elenco delle fotografie che hanno ricevuto segnalazioni e 
gestire ciascuna segnalazione decidendo se eliminare la fotografia oppure mantenerla.
21. Gli amministratori possono visualizzare statistiche in tempo reale sul sistema (e.g. numero di utenti, 
numero di accessi, numero di ricerche, di recensioni, di itinerari, etc..)
22. Gli amministratori possono inviare email promozionalisu accessori per escursionisti a tutti gli iscritti.
23. Gli amministratori possono arbitrariamente rimuovere o modificare itinerari inseriti dagli utenti. In 
questo caso, la schermata di dettaglio degli itinerari modificati mostrano un warning che informa gli 
utenti della modifica e della data in cui è avvenuta.
Tutte le funzionalità del sistema devono essere disponibili utilizzando il Client mobile. È altresì auspicabile
che il back-end sia messo in opera utilizzando tecnologie allo stato dell’arte quali ad esempio servizi di public
Cloud Computing come Azure o AWS, al fine di massimizzare la scalabilità del sistema in vista di un possibile 
repentino aumento del numero degli utenti nelle fasi iniziali di rilascio al pubblico.
