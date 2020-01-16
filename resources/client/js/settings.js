function pageLoad() {
    checkToken(
        /*success and fail are defined using arrow notation when checkToken is called.*/
        () => {},
        () => {window.location.href = "/client/login.html";}
    );
    document.getElementById("mainMenuOption").addEventListener("click", ()=> window.location.href = "/client/index.html");
}