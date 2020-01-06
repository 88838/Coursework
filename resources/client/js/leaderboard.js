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
                '<th class="killHeadings">blort</th>' +
                '<th class="killHeadings">droborg</th>' +
                '<th class="killHeadings">drogon</th>' +
            '</tr>' +
        '</table>';
    document.getElementById("leaderboardDiv").innerHTML = leaderboardHTML;
    /*    fetch('/players/list', {method: 'get'}

        ).then(response => response.json()
        ).then(players => {


        }*/
}