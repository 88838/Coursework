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