function pageLoad() {
    checkToken(
        /*success and fail are defined using arrow notation when checkToken is called.*/
        () => {},
        () => {window.location.href = "/client/login.html";}
    );
    document.getElementById("mainMenuOption").addEventListener("click", ()=> window.location.href = "/client/index.html");
    document.getElementById("changeUsernameOption").addEventListener("click", function() {showDiv("changeUsername");});
}
function showDiv(optionType) {
    /*depending on the parameter that has been passed in, the various elements are hidden or shown*/
    if(optionType === "changeUsername"){
        /*an element with the display style "none" are unique because all of them actually disappear instead of being hidden away*/
        /*this allows for other elements to take their place, instead of being put below the hidden elements*/
        document.getElementById("changeUsernameOption").style.display = "none";
        document.getElementById("changeUsernameForm").style.display = "block";
        document.getElementById("changePasswordOption").style.display = "none";
        document.getElementById("deleteAccountOption").style.display = "none";
        document.getElementById("logoutOption").style.display = "none";
        document.getElementById("muteOption").style.display = "none";
    }
}