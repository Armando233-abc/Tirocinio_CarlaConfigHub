# CarlaConfigHub

**CarlaConfigHub** è un sistema distribuito basato su un'architettura a microservizi, progettato per automatizzare il processo di generazione di file di configurazione XML compatibili con il simulatore CARLA.

Il sistema fornisce un'interfaccia intuitiva attraverso la quale gli utenti possono definire i parametri di simulazione. Una volta impostati i dati, il sistema coordina automaticamente la comunicazione tra i diversi servizi specializzati per produrre un documento XML strutturato e validato.

---

## Scopo del Sistema

L'obiettivo principale del progetto è astrarre la complessità della creazione manuale dei file di configurazione.
Questo approccio modulare consente di:

* **Ridurre la Complessità:** Semplifica la creazione di scenari di simulazione senza dover conoscere la sintassi XML specifica.
* **Minimizzare gli Errori:** Elimina il rischio di errori manuali e di formattazione.
* **Garantire la Conformità:** Assicura che l'output rispetti rigorosamente gli schemi richiesti dal simulatore CARLA.
* **Facilitare l'Estensione:** La struttura a microservizi permette l'aggiunta futura di nuove funzionalità senza impattare sull'intero sistema.

---

## I Microservizi

Il sistema è composto da unità funzionali indipendenti, ognuna responsabile di una specifica parte del processo di generazione:

| Servizio | Descrizione e Responsabilità |
| :--- | :--- |
| **`Config Service`** | **Interfaccia Utente.** Gestisce il frontend attraverso cui l'utente definisce i parametri di simulazione. |
| **`Gateway Service`** | **Orchestratore.** Punto di ingresso per le richieste API. Smista i dati ai microservizi competenti e raccoglie le risposte. Implementa il pattern Circuit Breaker. |
| **`Weather`** | **Generatore Meteo.** Elabora i parametri meteorologici per produrre il frammento XML relativo alle condizioni atmosferiche. |
| **`Vehicle`** | **Configuratore Veicolo.** Gestisce le specifiche tecniche del veicolo Ego e ne genera la configurazione corrispondente. |
| **`Scenario`** | **Gestore Ambiente.** Definisce i parametri generali dello scenario, inclusa la mappa e il tempo di simulazione. |
| **`Composer`** | **Assemblatore.** Riceve dal Gateway Service i frammenti XML degli altri servizi, li aggrega in un unico documento e valida la struttura finale. |

---

## Struttura della Repository

L'organizzazione delle cartelle riflette l'architettura del sistema:

```text
Tirocinio_CarlaConfigHub/
├── Composer/           # Servizio di assemblaggio e validazione XML
├── ConfigService/      # Frontend/Interfaccia di configurazione
├── GatewayService/     # API Gateway e Orchestrator
├── Scenario/           # Microservizio parametri Scenario
├── Vehicle/            # Microservizio parametri Veicolo
├── Weather/            # Microservizio parametri Meteo
├── docker-compose.yml  # Orchestrazione dei container
└── Jenkinsfile         # Pipeline CI/CD
```

## Gestione della Resilienza e Comunicazione
Per garantire la stabilità del sistema distribuito, il **Gateway Service** implementa il pattern **Circuit Breaker**.

Questo meccanismo monitora costantemente lo stato di salute dei microservizi. Se un servizio rileva un alto tasso di errori o tempi di risposta eccessivi, il circuito si "apre", bloccando temporaneamente le richieste verso quel componente specifico.

## Come avviare l'applicazione

Il progetto è predisposto per l'avvio rapido tramite **Docker Compose**.

### Prerequisiti
* [Docker Desktop](https://www.docker.com/products/docker-desktop) installato e in esecuzione.

### Procedura di Avvio
1.  **Clona e accedi alla cartella:**
   Esegui i comandi per scaricare il progetto e posizionarti nella directory corretta:
    ```bash
    git clone https://github.com/TuoUsername/Tirocinio_CarlaConfigHub.git
    cd Tirocinio_CarlaConfigHub
    ```
   
3.  **Avvia i servizi:**
    Lancia il seguente comando per costruire le immagini e avviare i container.
    ```bash
    docker-compose up --build
    ```
    *Nota: Se è la prima volta che lo esegui, potrebbe richiedere qualche minuto per scaricare le dipendenze.*

4.  **Accedi all'Applicazione:**
    Una volta che vedi i log scorrere e i servizi sono attivi, apri il browser all'indirizzo:
    * `http://localhost:8081`

### Come chiudere l'applicazione
Esistono due modi principali per terminare l'esecuzione:

* **Metodo 1: `CTRL + C` (Stop)**
    Premendo `CTRL + C` nel terminale dove è in esecuzione Docker, invii un segnale di interruzione e ferma l'esecuzione dei container.
    * I container rimangono esistenti e la rete Docker creata rimane attiva. È utile se vuoi riavviare velocemente in seguito senza ricreare tutto.

* **Metodo 2: `docker-compose down`**
    Apri il terminale nella cartella del progetto ed esegui:
    ```bash
    docker-compose down
    ```
    * Ferma i container e **li rimuove** completamente, eliminando anche la rete virtuale creata. È utile quando hai finito di lavorare per liberare risorse.
   

## Tecnologie utilizzate
* **Spring Boot** – Framework utilizzato per l'implementazione.
* **Docker** – Per la containerizzazione e l'orchestrazione.
* **Swagger** – Per la documentazione e il testing interattivo delle API.
* **Jenkins** – Per l'automazione della pipeline CI/CD.
