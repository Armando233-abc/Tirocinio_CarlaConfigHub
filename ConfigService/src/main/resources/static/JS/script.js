function inviaRichiestaGenerazione() {
    let btn = document.getElementById("btnGenera");
    let resultBox = document.getElementById("risultatoContainer");
    let resultText = document.getElementById("risultatoTesto");
    // 2. Chiamata AJAX
    fetch('/Home/genera', {
        method: 'POST',
        headers: {
            'Content-Type': 'text/plain'
        },
        body: resultBox
    })
        .then(response => response.text())
        .then(data => {
            resultText.innerText = data;
            resultBox.style.display = "block";
        })
        .catch(error => alert("Errore: " + error))
        .finally(() => {
            btn.disabled = false;
            btn.innerText = "Avvia Generazione XML";
        });
}