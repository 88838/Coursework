function pageLoad() {
    /*if the token is undefined, or in other words, there is not token, then the player will be redirected to the login.html page*/
    checkToken(
        /*success and fail are defined using arrow notation when checkToken is called.*/
        () => {},
        () => {window.location.href = "/client/login.html";}
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


