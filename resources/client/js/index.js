function pageLoad() {

    if (Cookies.get("token") === undefined) window.location.href = "/client/login.html";

}


