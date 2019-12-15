
function checkToken(success, fail) {
    /*the token is grabbed from the browser*/
    const token = Cookies.get("token");
    /*if the token is undefined, fail() runs*/
    if (token === undefined) fail();

/*    const formData = new FormData();
    formData.append("token", token);

    fetch("/players/checkToken", {method: 'post', data: formData}*/
    /*the token doesn't need to be passed as a parameter because the web browser sends cookies automatically*/
    fetch("/players/checkToken", {method: 'get'}
    ).then(response => response.json()
    ).then(responseData => {
        if (responseData.hasOwnProperty("error")) {
            fail();
        } else {
            responseData.status === "OK" ? success() : fail();
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


