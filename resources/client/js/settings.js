function pageLoad() {
    checkToken(
        () => {},
        () => {window.location.href = "/client/login.html";}
    );
    document.getElementById("mainMenuOption").addEventListener("click", ()=> window.location.href = "/client/index.html");
    /*similar to the login page, the function showDiv is used to change what is being shown on the page*/
    document.getElementById("changeUsernameOption").addEventListener("click", function() {showDiv("changeUsername");});
    document.getElementById("changePasswordOption").addEventListener("click", function() {showDiv("changePassword");});
    document.getElementById("deleteAccountOption").addEventListener("click", function() {showDiv("deleteAccount");});
    document.getElementById("cancelOption").addEventListener("click", function() {showDiv("cancelOption");});

    /*depending on the music cookie, the mute or unmute option is shown*/
    if(Cookies.get("music")==="true"){
        document.getElementById("muteOption").style.display = "block";
    }else if(Cookies.get("music")==="false"){
        document.getElementById("unmuteOption").style.display = "block";
    }
    /*the music cookie is set to false if the player clicks mute, and true if they player clicks unmute*/
    document.getElementById("muteOption").addEventListener("click", () =>{
        Cookies.set("music", "false");
        document.getElementById("muteOption").style.display = "none";
        document.getElementById("unmuteOption").style.display = "block";
    });
    document.getElementById("unmuteOption").addEventListener("click", () =>{
        Cookies.set("music", "true");
        document.getElementById("muteOption").style.display = "block";
        document.getElementById("unmuteOption").style.display = "none";
    });

    document.getElementById("changeUsernameConfirm").addEventListener("click", function() {processPlayerData("changeUsername");});
    document.getElementById("changePasswordConfirm").addEventListener("click", function() {processPlayerData("changePassword");});
    document.getElementById("deleteAccountConfirm").addEventListener("click", function() {processPlayerData("deleteAccount");});
    document.getElementById("logoutOption").addEventListener("click", function() {processPlayerData("logout");});

}
function showDiv(optionType) {
    if(optionType === "changeUsername"){
        document.getElementById("settingsDiv").style.display = "none";
        document.getElementById("cancelOption").style.display = "block";
        document.getElementById("changeUsernameForm").style.display = "block";
    }else if(optionType === "changePassword"){
        document.getElementById("settingsDiv").style.display = "none";
        document.getElementById("cancelOption").style.display = "block";
        document.getElementById("passwordForm").style.display = "block";
        document.getElementById("deleteAccountConfirm").style.display = "none";
        document.getElementById("changePasswordConfirm").style.display = "block";
    }else if(optionType === "deleteAccount"){
        document.getElementById("settingsDiv").style.display = "none";
        document.getElementById("cancelOption").style.display = "block";
        document.getElementById("passwordForm").style.display = "block";
        document.getElementById("changePasswordConfirm").style.display = "none";
        document.getElementById("deleteAccountConfirm").style.display = "block";
    }else if(optionType === "cancelOption"){
        document.getElementById("settingsDiv").style.display = "block";
        document.getElementById("cancelOption").style.display = "none";
        document.getElementById("changeUsernameForm").style.display = "none";
        document.getElementById("passwordForm").style.display = "none";
    }
}
function processPlayerData(processType) {
    let username = document.getElementById("usernameInput").value;
    let password = document.getElementById("passwordInput").value;
    let passwordRegExp = /(?=.+[a-z])(?=.+[A-Z])(?=.+[0-9])(?=.{8,})/;
    let fixedUsername = username.replace(/\s+/g, "")
    let usernameRegExp = /(?=.{1,16})/;

    if(processType==="changeUsername"){
        if(!usernameRegExp.test(fixedUsername)) {
            alert("error: Username must be at least 1 character and less than 16 characters")
            return;
        }
        /*the form is taken as the changeUsernameForm*/
        const form = document.getElementById("changeUsernameForm");
        const formData = new FormData(form);
        fetch("/players/changeUsername", {method: 'post', body: formData}
        ).then(response => response.json()
        ).then(responseData => {
            if (responseData.hasOwnProperty('error')) alert(responseData.error);
            /*the page is reloaded once the player has changed the username to make sure everything is saved properly*/
            location.reload();
        });
    }else if(processType==="changePassword"){
        if(!passwordRegExp.test(password)) {
            alert("error: Password must be bigger than 8 characters, contain an uppercase and lowercase letter, contain a digit.")
            return;
        }
        /*the changePassword and deleteAccount options both user the passwordForm*/
        const form = document.getElementById("passwordForm");
        const formData = new FormData(form);
        fetch("/players/changePassword", {method: 'post', body: formData}
        ).then(response => response.json()
        ).then(responseData => {
            if (responseData.hasOwnProperty('error')) alert(responseData.error);
            location.reload();
        });
    }else if(processType==="deleteAccount") {
        const form = document.getElementById("passwordForm");
        const formData = new FormData(form);
        fetch("/players/delete", {method: 'post', body: formData}
        ).then(response => response.json()
        ).then(responseData => {
            if (responseData.hasOwnProperty('error')) alert(responseData.error);
            location.reload();
        });
    }else if(processType==="logout") {
        /*when the player logs out, the cookies are removed, and the player is redirected to the login page*/
        Cookies.remove("token");
        Cookies.remove("music");
        window.location.href = "/client/login.html";
    }
}