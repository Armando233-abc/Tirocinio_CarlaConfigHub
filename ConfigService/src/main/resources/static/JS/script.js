// Inizializzazione al caricamento della pagina
document.addEventListener('DOMContentLoaded', () => {
    // Configurazione dei slider con aggiornamento valori in tempo reale
    const sliders = [
        { id: 'cloudiness', suffix: '%' },
        { id: 'precipitation', suffix: '%' },
        { id: 'sunAltitude', suffix: '°' },
        { id: 'trafficDensity', suffix: '%' }
    ];

    sliders.forEach(config => {
        const input = document.getElementById(config.id);
        const display = document.getElementById(`val-${config.id}`);

        if (input && display) {
            input.addEventListener('input', (e) => {
                display.innerText = e.target.value + config.suffix;
            });
        }
    });
});

// Gestione submit del form
document.getElementById('configForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    // Riferimenti agli elementi UI
    const placeholder = document.getElementById('placeholderState');
    const loadingArea = document.getElementById('loadingArea');
    const resultArea = document.getElementById('resultArea');
    const errorArea = document.getElementById('errorArea');
    const downloadBtn = document.getElementById('downloadBtn');

    // Mostra stato di caricamento
    showLoadingState();

    // Preparazione dati per la richiesta
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
        // Piccolo ritardo per visualizzare l'animazione di caricamento
        await new Promise(resolve => setTimeout(resolve, 800));

        // Chiamata API al gateway
        const response = await fetch('http://gateway-service:8080/api/gateway/generate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Errore di connessione al Gateway");
        }

        const xmlResult = await response.text();

        // Mostra risultato con successo
        showResultState(xmlResult);

    } catch (error) {
        // Mostra stato di errore
        showErrorState(error.message);
    }

    // Funzioni helper per gestire gli stati UI
    function showLoadingState() {
        placeholder.classList.add('hidden');
        resultArea.classList.add('hidden');
        errorArea.classList.add('hidden');
        downloadBtn.classList.add('hidden');
        loadingArea.classList.remove('hidden');
    }

    function showResultState(xmlContent) {
        loadingArea.classList.add('hidden');
        document.getElementById('xmlOutput').value = xmlContent;
        resultArea.classList.remove('hidden');
        downloadBtn.classList.remove('hidden');
    }

    function showErrorState(errorMessage) {
        loadingArea.classList.add('hidden');
        document.getElementById('errorMessage').innerText = "Errore: " + errorMessage;
        errorArea.classList.remove('hidden');
    }
});

// Gestione download del file XML
document.getElementById('downloadBtn').addEventListener('click', () => {
    const xmlContent = document.getElementById('xmlOutput').value;

    // Creazione blob e download
    const blob = new Blob([xmlContent], { type: "text/xml" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");

    link.href = url;
    link.download = "carla_scenario_config.xml";
    link.click();

    // Pulizia dell'URL temporaneo
    URL.revokeObjectURL(url);
});