document.getElementById('configForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    // Reset messaggi precedenti
    document.getElementById('resultArea').classList.add('hidden');
    document.getElementById('errorArea').classList.add('hidden');

    // 1. Raccolta Dati e Creazione Struttura JSON
    // Deve corrispondere alla Map<String, Object> che si aspetta il GatewayService
    const requestData = {
        weatherParams: {
            cloudiness: document.getElementById('cloudiness').value,
            precipitation: document.getElementById('precipitation').value,
            windIntensity: document.getElementById('windIntensity').value,
            sunAltitudeAngle: document.getElementById('sunAltitude').value
        },
        vehicleParams: {
            model: document.getElementById('vehicleModel').value,
            color: document.getElementById('vehicleColor').value,
            autopilot: document.getElementById('autopilot').value
        },
        scenarioParams: {
            town: document.getElementById('town').value,
            pedestrians: document.getElementById('pedestrians').value,
            trafficDensity: document.getElementById('trafficDensity').value
        }
    };

    try {
        // 2. Chiamata al Gateway Service
        // Assicurati che il Gateway sia attivo su localhost:8080
        const response = await fetch('http://localhost:8080/api/gateway/generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });

        // 3. Gestione Risposta
        if (!response.ok) {
            // Se c'è un errore (es. validazione fallita o circuit breaker aperto)
            const errorMsg = await response.text();
            throw new Error(errorMsg || `Errore HTTP: ${response.status}`);
        }

        const xmlResult = await response.text();

        // Mostra risultato
        document.getElementById('xmlOutput').value = xmlResult;
        document.getElementById('resultArea').classList.remove('hidden');

    } catch (error) {
        // Mostra errore
        document.getElementById('errorMessage').innerText = "Errore: " + error.message;
        document.getElementById('errorArea').classList.remove('hidden');
    }
});

// Funzione per scaricare il file XML generato
document.getElementById('downloadBtn').addEventListener('click', function() {
    const text = document.getElementById('xmlOutput').value;
    const blob = new Blob([text], { type: "text/xml" });
    const anchor = document.createElement("a");
    anchor.href = URL.createObjectURL(blob);
    anchor.download = "carla_config.xml";
    anchor.click();
});