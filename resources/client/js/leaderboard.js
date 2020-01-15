function pageLoad(){
    let leaderboardHTML =
        '<table id="leaderboardTable">' +
            '<tr id="mainHeadingsRow">' +
                '<th class="mainHeadings">name</th>' +
                '<th class="mainHeadings">high score</th>' +
                '<th class="mainHeadings" colspan="5">kills</th>' +
            '</tr>' +
            '<tr id="killHeadingsRow">' +
                '<th></th>' +
                '<th></th>' +
                '<th class="killHeadings">blarp</th>' +
                '<th class="killHeadings">bloof</th>' +
                '<th class="killHeadings">droborg</th>' +
                '<th class="killHeadings">drogon</th>' +
            '</tr>';

    fetch('/players/list', {method: 'get'}
    ).then(response => response.json()
        /*this must be called playerDb as to not get confused with the player object that is being used in the game*/
    ).then(playersDb => {
        if (playersDb.hasOwnProperty('error')) alert(responseData.error);
        /*the player is created using the parameters of the playerid and skinid from the database*/
        for (let playerDb of playersDb) {
            leaderboardHTML +=
                `<tr>` +
                `<td>${playerDb.username}</td>` +
                `<td>${playerDb.highScore}</td>`;
            try {
                for (let kills of playerDb.kills) {
                        leaderboardHTML +=
                            `<td>${kills.numberOfKills}</td>`;
                }
            }catch{}
            leaderboardHTML +=
                `</tr>`;



/*            let killsDb = playerDb.kills;
                for(let killDb of killsDb) {
                    leaderboardHTML += `<td>${killsDb.numberOfKills}</td>`;
                }*/
        }
        leaderboardHTML += '</table>';
        document.getElementById("leaderboardDiv").innerHTML = leaderboardHTML;
    });


}