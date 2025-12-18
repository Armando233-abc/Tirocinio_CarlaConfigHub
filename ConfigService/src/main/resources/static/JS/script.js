document.getElementById("configForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const payload = {
        weather: {
            rain: document.getElementById("rain").value,
            cloudiness: document.getElementById("cloudiness").value
        },
        vehicle: {
            model: document.getElementById("model").value,
            speed: document.getElementById("speed").value
        },
        scenario: {
            map: document.getElementById("map").value,
            duration: document.getElementById("duration").value
        }
    };

    try {
        const response = await fetch("/config/generate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const text = await response.text();
        document.getElementById("output").textContent = text;

    } catch (error) {
        document.getElementById("output").textContent =
            "Errore nella comunicazione con il Config Service";
    }
});
