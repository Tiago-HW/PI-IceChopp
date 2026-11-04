const telaIdade = document.getElementById("verificacao-idade");
const telaNegado = document.getElementById("acessoNegado");

// Verifica se o visitante já confirmou a idade
if (localStorage.getItem("idadeConfirmada") === "sim") {
    telaIdade.style.display = "none";
}

// Caso tenha 18 anos ou mais
function entrarSite() {
    localStorage.setItem("idadeConfirmada", "sim");

    telaIdade.style.display = "none";
}

// Caso seja menor de idade
function sairSite() {
    window.location.href = "../pages/acessoNegado.html";
}
localStorage.removeItem("idadeConfirmada");