function checkToken(success, fail) {

    const token = Cookies.get("token");
    if (token === undefined) fail();

    fetch("/players/checkToken", {method: 'get'}
    ).then(response => response.json()
    ).then(data => {
        if (data.hasOwnProperty("error")) {
            fail();
        } else {
            data.status === "OK" ? success() : fail();
        }
    });
}
function pageLoad() {
    /*if the token is undefined, or in other words, there is not token, then the player will be redirected to the login.html page*/
    checkToken(
        () => {},
        () => {
            window.location.href = "/client/login.html";
        }
    );

    document.getElementById("startOption").addEventListener("click", function () {optionHandler("start");});
}

function optionHandler(optionChosen){
    if(optionChosen=="start"){
        window.location.href = "/client/game.html";
    }
}


