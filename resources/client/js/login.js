function pageLoad() {
    document.getElementById("loginOption").addEventListener("click", showLogin);
}

function showLogin() {
    document.getElementById("loginDiv").style.display = "block";
    document.getElementById("optionsLogin").style.display = "none";
    document.getElementById("logoDiv").style.display = "none";
}

