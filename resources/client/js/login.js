function pageLoad() {
    /*the parameter that is passed into the function depends on which option the player has clicked on */
    document.getElementById("loginOption").addEventListener("click", function() {showDiv("login");});
    document.getElementById("signUpOption").addEventListener("click", function() {showDiv("signUp");});
    document.getElementById("cancelOption").addEventListener("click",function() {showDiv("cancel");});

    document.getElementById("loginConfirm").addEventListener("click", function() {processPlayerData("login");});
    document.getElementById("signUpConfirm").addEventListener("click", function() {processPlayerData("signUp");});
}

function showDiv(optionType) {
    /*depending on the parameter that has been passed in, the various elements are hidden or shown*/
    if( optionType == "login"){
        /*an element with the display style "none" are unique because all of them actually disappear instead of being hidden away*/
        /*this allows for other elements to take their place, instead of being put below the hidden elements*/
        document.getElementById("loginDiv").style.display = "none";
        document.getElementById("loginForm").style.display = "block";
        document.getElementById("signUpConfirm").style.display = "none";
        document.getElementById("loginConfirm").style.display = "block";
    }else if(optionType == "signUp"){
        document.getElementById("loginDiv").style.display = "none";
        document.getElementById("loginForm").style.display = "block";
        document.getElementById("signUpConfirm").style.display = "block";
        document.getElementById("loginConfirm").style.display = "none";
    }else if(optionType =="cancel"){
        document.getElementById("loginDiv").style.display = "block";
        document.getElementById("loginForm").style.display = "none";
        document.getElementById("signUpConfirm").style.display = "none";
        document.getElementById("loginConfirm").style.display = "none";
    }
}

function processPlayerData(processType) {

    const form = document.getElementById("loginForm");
    const formData = new FormData(form);

    if(processType=="login"){
        login("/players/login", formData);
    }else if(processType=="signUp"){
        fetch("/players/new", {method: 'post', body: formData}
        ).then(response => response.json()
        ).then(responseData => {

            if (responseData.hasOwnProperty('error')) {
                alert(responseData.error);
            } else {
                login("/players/login", formData);
            }
        });
    }


}
function login(apiPath, formData){
    fetch(apiPath, {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(responseData => {

        if (responseData.hasOwnProperty('error')) {
            alert(responseData.error);
        } else {
            Cookies.set("token", responseData.Token);

            window.location.href = '/client/index.html';
        }
    });
}



