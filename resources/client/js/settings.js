function pageLoad() {
    checkToken(
        /*success and fail are defined using arrow notation when checkToken is called.*/
        () => {},
        () => {window.location.href = "/client/login.html";}
    );
    document.getElementById("mainMenuOption").addEventListener("click", ()=> window.location.href = "/client/index.html");
    document.getElementById("changeUsernameOption").addEventListener("click", function() {showDiv("changeUsername");});
    document.getElementById("changePasswordOption").addEventListener("click", function() {showDiv("changePassword");});
    document.getElementById("deleteAccountOption").addEventListener("click", function() {showDiv("deleteAccount");});
    document.getElementById("logoutOption").addEventListener("click", () =>{
        Cookies.remove("token");
        Cookies.remove("music");
        window.location.href = "/client/login.html";
    });
    if(Cookies.get("music")==="true"){
        document.getElementById("muteOption").style.display = "block";
    }else if(Cookies.get("music")==="false"){
        document.getElementById("unmuteOption").style.display = "block";
    }
    document.getElementById("muteOption").addEventListener("click", () =>{
        Cookies.set("music", "false")
        document.getElementById("muteOption").style.display = "none";
        document.getElementById("unmuteOption").style.display = "block";
    });
    document.getElementById("unmuteOption").addEventListener("click", () =>{
        Cookies.set("music", "true")
        document.getElementById("muteOption").style.display = "block";
        document.getElementById("unmuteOption").style.display = "none";
    });

    document.getElementById("cancelOption").addEventListener("click", function() {showDiv("cancelOption");});
    document.getElementById("changeUsernameConfirm").addEventListener("click", function() {processPlayerData("changeUsername");});
    document.getElementById("changePasswordConfirm").addEventListener("click", function() {processPlayerData("changePassword");});
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
    if(processType==="changeUsername"){
        const form = document.getElementById("changeUsernameForm");
        const formData = new FormData(form);
        fetch("/players/changeUsername", {method: 'post', body: formData}

        ).then(response => response.json()
        ).then(responseData => {
            if (responseData.hasOwnProperty('error')) alert(responseData.error);
            location.reload();
        });
    }else if(processType==="changePassword"){
        const form = document.getElementById("passwordForm");
        const formData = new FormData(form);
        fetch("/players/changePassword", {method: 'post', body: formData}

        ).then(response => response.json()
        ).then(responseData => {
            if (responseData.hasOwnProperty('error')) alert(responseData.error);
            location.reload();
        });
    }
}