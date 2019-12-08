function pageLoad() {
    /*if the token is undefined, or in other words, there is not token, then the player will be redirected to the login.html page*/
    if (Cookies.get("token") === undefined) window.location.href = "/client/login.html";
}


