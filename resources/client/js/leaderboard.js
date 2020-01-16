function pageLoad(){
    checkToken(
        /*success and fail are defined using arrow notation when checkToken is called.*/
        () => {},
        () => {window.location.href = "/client/login.html";}
    );
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
    let playerCount = 0;
    fetch('/players/list', {method: 'get'}
    ).then(response => response.json()
    ).then(playersDb => {
        if (playersDb.hasOwnProperty('error')) alert(responseData.error);
        for (let playerDb of playersDb) {
            /*for each player, the player count is incremented*/
            playerCount++;
            let highScore;
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

        table = document.getElementById("leaderboardTable");
        let rows;
        let sorted = false;
        /*the loop continues until sorted is equal to true*/
        while(!sorted) {
            /*if no swaps happen within this loop, then the table is sorted and sorted will stay true*/
            sorted = true;
            /*the rows is assigned to the current order of the table rows*/
            rows = table.rows;
            /*the first two rows are headings, and because rows are incremented like arrays, they start with an index of 0*/
            /*the third row will therefore have an index of 2*/
            /*the for loop goes through all of the players*/
            for (let i = 2; i <= playerCount; i++) {
                /*the second element of the row is the high score, and has an index of 1*/
                let x = rows[i].getElementsByTagName("td")[1].innerHTML;
                /*if the player doesn't have a high score yet, then it's set to 0*/
                if(x === "") x = 0;
                /*the highScore needs to be parsed in order to compare it as an integer instead of the ascii value*/
                x = parseInt(x);
                let y = rows[i + 1].getElementsByTagName("td")[1].innerHTML;
                if(y === "") y = 0;
                y = parseInt(y);
                /*ascending order is needed so if the current high score is smaller than the next high score, then a swap occurs*/
                if (x < y) {
                    /*the parentNode is the table*/
                    /*the row with the bigger high score is inserted before the row with the smaller highScore*/
                    rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
                    /*sorted is set to false, since a swap took place*/
                    sorted = false;
                }
            }
        }
        /*there need to be only 10 players displayed, so 12 rows including the headings*/
        while(table.rows.length >12){
            /*the 13th row is deleted*/
            table.deleteRow(12);
        }
    });
}