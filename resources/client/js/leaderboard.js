function pageLoad(){
    document.getElementById("mainMenuOption").addEventListener("click", ()=> window.location.href = "/client/index.html");
    /*all of the html that will be in the leaderboardDiv will go into the leaderboardHTML variable*/
    let leaderboardHTML =
        '<table id="leaderboardTable">' +
        /*these are the headings at the top of the leaderboard*/
            '<tr id="mainHeadingsRow">' +
                '<th class="mainHeadings">username</th>' +
                '<th class="mainHeadings">high score</th>' +
                /*kills have a colspan (column span) of 4 because there are 4 monsters*/
                '<th class="mainHeadings" colspan="4">kills</th>' +
            '</tr>' +
            '<tr id="killHeadingsRow">' +
                /*two blank headings are needed, underneath the name and the highscore*/
                '<th></th>' +
                '<th></th>' +
                '<th class="killHeadings">blarp</th>' +
                '<th class="killHeadings">bloof</th>' +
                '<th class="killHeadings">droborg</th>' +
                '<th class="killHeadings">drogon</th>' +
            '</tr>';

    fetch('/players/list', {method: 'get'}
    ).then(response => response.json()
    ).then(playersDb => {
        if (playersDb.hasOwnProperty('error')) alert(responseData.error);
        for (let playerDb of playersDb) {
            let highScore
            /*if the high score is null, it's replaced with 0*/
            if(playerDb.highScore===null){
                highScore = "";
            }else{
                highScore = playerDb.highScore
            }
            leaderboardHTML +=
                `<tr class ="playerDataRow">` +
                /*the data cells will have the player username and data*/
                `<td>${playerDb.username}</td>` +
                `<td>${highScore}</td>`;
            try {
                for (let kills of playerDb.kills) {
                    leaderboardHTML +=
                        /*the kills are filled with the number of kills that the player has for each monster*/
                        /*the monsters appear chronologically in the game, they will be stored in order in the database as well*/
                        `<td>${kills.numberOfKills}</td>`;
                }
            }catch{}
            leaderboardHTML += `</tr>`;
        }
        leaderboardHTML += '</table>';
        /*the leaderboardHTML is then inserted into the innerHTML of the leaderboard div*/
        document.getElementById("leaderboardDiv").innerHTML = leaderboardHTML;
    });


}