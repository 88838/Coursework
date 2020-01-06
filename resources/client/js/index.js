/*two functions are passed as parameters*/
function checkToken(success, fail) {
    const token = Cookies.get("token");
    /*if the token is undefined, fail() runs*/
    if (token === undefined) fail();

    /*the token doesn't need to be passed into the api fetch method because the web browser sends cookies automatically*/
    fetch("/players/checkToken", {method: 'get'}
    ).then(response => response.json()
    ).then(responseData => {
        if (responseData.hasOwnProperty("error")) {
            fail();
        } else {
            /*a ternary operator is used here*/
            /*if the status is OK then success() is run, and if it's not then fail() is run*/
            responseData.status === "OK" ? success() : fail();
        }
    });
}
function pageLoad() {
    /*if the token is undefined, or in other words, there is not token, then the player will be redirected to the login.html page*/
    checkToken(
        /*success and fail are defined using arrow notation when checkToken is called.*/
        () => {},
        () => {
            window.location.href = "/client/login.html";
        }
    );

    document.getElementById("startOption").addEventListener("click", function () {optionHandler("start");});
    document.getElementById("shopOption").addEventListener("click", function () {optionHandler("shop");});
    document.getElementById("leaderboardOption").addEventListener("click", function () {optionHandler("leaderboard");});
    document.getElementById("settingsOption").addEventListener("click", function () {optionHandler("settings");});
}

function optionHandler(optionChosen){
    if(optionChosen=="start"){
        window.location.href = "/client/game.html";
    }else if(optionChosen=="shop"){
        window.location.href = "/client/shop.html";
    }else if(optionChosen=="leaderboard"){
        window.location.href = "/client/leaderboard.html";
    }else if(optionChosen=="settings"){
        window.location.href = "/client/settings.html";
    }
}


