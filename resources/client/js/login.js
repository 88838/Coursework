function pageLoad() {
    /*the parameter that is passed into the function depends on which option the player has clicked on */
    document.getElementById("loginOption").addEventListener("click", function() {showDiv("login");});
    document.getElementById("signUpOption").addEventListener("click", function() {showDiv("signUp");});
    document.getElementById("cancelOption").addEventListener("click",function() {showDiv("cancel");});

    /*when the player confirms whichever option they chose, a function is run that processes the player data*/
    document.getElementById("loginConfirm").addEventListener("click", function() {processPlayerData("login");});
    document.getElementById("signUpConfirm").addEventListener("click", function() {processPlayerData("signUp");});
}

function showDiv(optionType) {
    /*depending on the parameter that has been passed in, the various elements are hidden or shown*/
    if( optionType === "login"){
        /*an element with the display style "none" are unique because all of them actually disappear instead of being hidden away*/
        /*this allows for other elements to take their place, instead of being put below the hidden elements*/
        document.getElementById("loginDiv").style.display = "none";
        document.getElementById("loginForm").style.display = "block";
        document.getElementById("signUpConfirm").style.display = "none";
        document.getElementById("loginConfirm").style.display = "block";
    }else if(optionType === "signUp"){
        document.getElementById("loginDiv").style.display = "none";
        document.getElementById("loginForm").style.display = "block";
        document.getElementById("signUpConfirm").style.display = "block";
        document.getElementById("loginConfirm").style.display = "none";
    }else if(optionType ==="cancel"){
        document.getElementById("loginDiv").style.display = "block";
        document.getElementById("loginForm").style.display = "none";
        document.getElementById("signUpConfirm").style.display = "none";
        document.getElementById("loginConfirm").style.display = "none";
    }
}

function processPlayerData(processType) {
    /*the form data from the html element is made into a constant, because it will not change during the process of the function*/
    const form = document.getElementById("loginForm");
    const formData = new FormData(form);
    /*if the processType is "login", then the login function will be run straight away, with the form data as the parameter*/
    if(processType==="login"){
        login(formData, processType);
    /*if the process type is "signUp then the /players/new api is first run*/
    }else if(processType==="signUp"){
        let username = document.getElementById("usernameInput").value;
        let password = document.getElementById("passwordInput").value;

        let fixedPassword = password.replace(/\s+/g, "");
        /*the regular expression uses a lookahead to check all characters for lowercase, uppercase letters and digits*/
        /*it also checks that the password is bigger than 8, with no upper boundary (that's why there is a comma)*/
        let passwordRegExp = /(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})/;
        /*if the password does not pass the test, then an error pops up and the function is returned */
        if(!passwordRegExp.test(fixedPassword)) {
            alert("error: Password must be bigger than 8 characters, contain an uppercase and lowercase letter, contain a digit.")
            return;
        }
        /*all the whitespace in the username is deleted*/
        let fixedUsername = username.replace(/\s+/g, "");
        let usernameRegExp = /(?=.{1,16})/;
        /*if the username is not between 1 and 16 characters after the whitespace is deleted, then an error pops up*/
        if(!usernameRegExp.test(fixedUsername)){
            alert("error: Username must be at least 1 character and less than 16 characters")
            return;
        }
        /*the method is a post method, so this needs to be specified, and the body of the api is the form data*/
        fetch("/players/new", {method: 'post', body: formData}
        /*this is a promise function that returns the response in json*/
        ).then(response => response.json()
        ).then(responseData => {
            /*if there is an error then an alert will pop up with the error*/
            if (responseData.hasOwnProperty('error')) {
                alert(responseData.error);
            } else {

                /*if there is no error then the login function will be run, after the player has been created*/
                /*this is so that the new player doesn't have to log in again after just signing up, which makes it easier for them*/
                login(formData, processType);
            }
        });
    }
}

/*the processType is now passed in*/
function login(formData, processType){
    /*like before, the api is fetched*/
    fetch("/players/login", {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(responseData => {

        if (responseData.hasOwnProperty('error')) {
            alert(responseData.error);
        } else {

            /*this time, a cookie is set in the browser, which is the randomly generated token from the api*/
            Cookies.set("token", responseData.token);
            /*a cookie is set for the player so if they are already logged in, the page knows whether they have the music turned on or not*/
            /*true has to be a string because cookies are always strings*/
            Cookies.set("music", "true");
            /*if the player is signing up then the skin with skinid 1 must be bought because it is the default skin, and must be unlocked*/
            if(processType==="signUp"){
                let formDataSkin = new FormData();
                formDataSkin.append("skinid", "1");
                fetch('/unlockedSkins/new/', {method: 'post', body: formDataSkin}
                ).then(response => response.json()
                ).then(unlockedSkinsDb => {
                    if (unlockedSkinsDb.hasOwnProperty('error')) alert(unlockedSkinsDb.error);
                    /*the shop info is refreshed to show which skin the player has bought, what skin is selected, and the currency is updated*/
                });
            }
            /*after the player has logged in, they will be redirected back to the main menu*/
            window.location.href = '/client/index.html';
        }
    });
}



