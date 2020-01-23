let gw = 1200, gh = 600, pw = 600, ph = 600;
let lastTimestamp = 0;

/*the keys that are pressed are stored as an object*/
const pressedKeys = {};
function randomX(){
    return (Math.random() * (pw - 64)) + 32;
}

function spawnMonster(){
    /*same as checking whether the skinid of the player matches the skinid of the skin, the stageid is checked against each monster in the info array*/
    for(let monsterInfo of monstersInfo){
        if(stage.stageid == monsterInfo[4]){
            if(monsterInfo[2]==="horizontal"){
                /*if the movement type is horizontal then the monster either has a velocity of -175 or (+)175*/
                monsters.push(new Monster(monsterInfo[0], monsterInfo[1], monsterInfo[3], monsterInfo[4], randomX(), ((Math.round(Math.random()) * 2) - 1) * 175));
            }else if(monsterInfo[2]==="stationary"){
                /*otherwise, if the type is stationary then the velocity is 0 */
                monsters.push(new Monster(monsterInfo[0], monsterInfo[1], monsterInfo[3], monsterInfo[4], randomX(), 0));
            }
        }
    }
}

function spawnProjectile(){
    for(let monster of monsters){
        /*if the current monster has an attackType of projectile, then the projectile is pushed in the middle of the monster, and it looks like they're shooting*/
        if(monster.attackType==="projectile"){
            projectiles.push(new Projectile(monster.x, monster.y))
        }
    }
}

function getDeaths(){
    fetch('/deaths/get/' + Cookies.get("token") , {method: 'get'}
    ).then(response => response.json()
    ).then(deathsDb => {
        if (deathsDb.hasOwnProperty('error')) alert(deathsDb.error);
        for (let deathDb of deathsDb) {
            /*all three deaths of the player are fetched and pushed*/
            deaths.push(new Death(deathDb.livesLeft, deathDb.deathLocationX, deathDb.deathLocationY))
        }
    });
}

function pageLoad(){
    checkToken(
        /*success and fail are defined using arrow notation when checkToken is called.*/
        () => {},
        () => {window.location.href = "/client/login.html";}
    );

    document.getElementById("mainMenuOption").addEventListener("click", ()=> window.location.href = "/client/index.html");
    /*if the key is pressed, it is set to true for that key*/
    /*once the key has been let go, it is set as false*/
    window.addEventListener("keydown", event => pressedKeys[event.key] = true);
    window.addEventListener("keyup", event => pressedKeys[event.key] = false);

    /*only once has loaded, is the first frame requested*/
    loadStagesInfo.then(() =>{
        loadSkinImages.then(() => {
            loadMonstersInfo.then(() => {
                loadStarImage.then(() => {
                    loadPlayer.then(() => {
                        player.setSkin();
                        /*a music object is made, using the audio file source*/
                        let music = new Music( "/client/audio/interstellar.m4a");
                        /*the music is played if the cookie is true*/
                        if(Cookies.get("music") === "true") music.play();

                        stage = new Stage(stagesInfo[0][0], stagesInfo[0][1]);
                        /*the deaths need to be pushed for the first time when the page first loads*/
                        /*if the player is alive, a new monster is pushed every 450 milliseconds*/
                        setInterval(() => {
                            if (player.alive) spawnMonster()
                        }, 350);
                        /*every 750 milliseconds, every monster that can, shoots*/
                        setInterval(() => {
                            if (player.alive) spawnProjectile()
                        }, 750);
                        /*like the monsters and the score, the setInterval function is used*/
                        /*one star will show up every 5 seconds*/
                        setInterval(() => {
                            if (player.alive) stars.push(new Star(randomX()))
                        }, 5000);

                        /*if the player is alive, their score is increased by 1 every 100 milliseconds (by 10 every second)*/
                        /*this is ongoing, so that even if the player doesn't kill any monsters or collects any currency, it still adds score for as long as they survive*/
                        setInterval(() => {
                            if (player.alive) player.score += 1
                        }, 100);

                        setInterval(() => {
                            if (player.alive) {
                                /*if the spriteFrame is the last frame, it resets*/
                                if (player.spriteFrame === 14) {
                                    player.spriteFrame = 11;
                                } else {
                                    /*otherwise, the sprite frame increases every 100 milliseconds, which animates the player*/
                                    player.spriteFrame++;
                                }
                            }
                        }, 100);
                        setInterval(() => {
                            if (player.alive) {
                                for (let monster of monsters) {
                                    /*the first monster only has 3 sprite frames*/
                                    if (monster.spriteFrame === 2 && monster.monsterid ===1) {
                                        monster.spriteFrame = 0;
                                    /*whereas the other monsters have 4 sprite frames*/
                                    }else if (monster.spriteFrame === 3 && (monster.monsterid ===2||monster.monsterid ===3||monster.monsterid ===4)) {
                                            monster.spriteFrame = 0;
                                    } else {
                                        monster.spriteFrame++;
                                    }
                                }
                            }
                        }, 250);
                        /*the gameFrame function has to be requested for the first time in when the page loads*/
                        window.requestAnimationFrame(gameFrame);

                    });
                });
            });
        });
    });
}

function gameFrame(timestamp) {
    /*since lastTimestamp is defined as 0, the if statement is true for the first time this function is run*/
    /*the lastTimestamp is now timestamp*/
    if (lastTimestamp === 0) lastTimestamp = timestamp;
    /*to get frame length in seconds it has to be divided by 1000 because it is in milliseconds originally.*/
    const frameLength = (timestamp - lastTimestamp) / 1000;
    lastTimestamp = timestamp;

    /*inputs, processes and outputs are run every frame*/
    /*frameLength is passed into the inputs and processes, because it will be used to update the objects*/
    inputs(frameLength);
    processes(frameLength);
    outputs();

    /*this is a javascript method that requests that the window calls a specific function to update an animation*/
    /*the function must call itself in order to animate another frame*/
    /*the number of callbacks is usually 60 time per second*/
    window.requestAnimationFrame(gameFrame);
}

function inputs(frameLength){
    /*the player has to be alive for the inputs to be processed*/
    if (player.alive){
        /*"a" or "A" will be true if they have been pressed*/
        /*the capital "A" is needed so that the player can still play even if they have accidentally pressed caps lock, which increases useability*/
        if(pressedKeys["a"]||pressedKeys["A"]){
            /*the player accelerates at a certain rate*/
            player.dx -= 10000*frameLength;
            /*the player's max velocity is 600 and -600*/
            if (player.dx < -700) player.dx = -700;
        }else if(pressedKeys["d"]||pressedKeys["D"]){
            player.dx += 10000*frameLength;
            if (player.dx > 700) player.dx = 700;
        } else{
            /*when no keys are pressed, the player decelerates at a certain rate*/
            /*the velocity is multiplied by a frameLength, which is a number less than 1, so it will decrease each frame until it reaches 0*/
            player.dx *= frameLength;
        }
        /*if the player presses the space bar, then they are attacking*/
        if(pressedKeys[" "]){
            /*nothing changes unless the cooldown is false*/
            if(!player.cooldown) {
                /*the cooldown starts once the spacebar is pressed and the player attacks*/
                /*the cooldownTimer counts down from 1 second*/
                player.cooldownTimer = 1;
                player.cooldown = true;
            }
        }else{
            player.attacking = false;
        }
    }
    /*if the player is dead, and the player presses the enter key, then most of the player attributes are reset*/
    /*this includes the lives, and the alive attribute, which then allows the stage to be drawn, and all of the monsters and stars to be pushed, along with the score being increased again*/
    if(!player.alive && pressedKeys["Enter"]){
        player.lives = 3;
        player.cumCurrency = 0;
        player.score = 0;
        player.artificialY = 0;
        player.x = pw/2;
        player.alive = true;
        getDeaths();
    }
}

function separation(entity1, entity2) {
    /*this is the javascript version of the separation function created in Phase 3 Design*/
    /*the Math object is used to work out the square root and power of different numbers*/
    return Math.sqrt(Math.pow(entity1.x - entity2.x, 2) /* the 2 is the power*/
        + Math.pow(entity1.y - entity2.y, 2));
}

function saveDeath(){
    /*because there isn't a physical form to be taken from the page, a FormData object must be made*/
    /*the cookie doesn't need to be in the formData because it is sent by default*/
    let formData = new FormData();
    /*a formData parameter must be string, so all the values must be converted to strings*/
    formData.append("livesLeft", (player.lives).toString());
    /*the x locations must be rounded, otherwise the api will not be able to parse it to an integer because it will be a double*/
    formData.append("deathLocationX", (Math.round(player.x)).toString());
    formData.append("deathLocationY", (Math.round(player.artificialY)).toString());
    formData.append("stageid", stage.stageid.toString());

    fetch('/deaths/update/', {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(responseData => {
        /*to add extra validation, an alert will display if there is an error*/
        if (responseData.hasOwnProperty('error')) alert(responseData.error);
    });
}

function playerDeath(){
    /*all the monsters are killed*/
    for (let monster of monsters) monster.alive = false;
    for (let star of stars) star.active = false;
    for (let projectile of projectiles) projectile.active = false;
    for (let death of deaths) death.active = false;
    player.lives -= 1;
    /*the kills are saved to the database after the player dies*/
    saveKills();
    /*the kills array is emptied, as each life is a 'session'*/
    kills = [];

    /*saveDeath must be called after the player lives have decreased because the attribute in the database is livesLeft rather than lives*/
    saveDeath();
    /*getDeaths must be called after the last death is saved, so that it is displayed*/
    getDeaths();
    /*the currency is saved, and the currency is reset to 0*/
    saveCurrency();
    player.currency=0;
    /*because this is after lives have decreased, it now has to be zero for the player to be fully dead*/
    if (player.lives === 0){
        player.alive = false;
        /*this has do be done after the player has died so that the score doesn't keep updating every 100 ms*/
        saveHighScore();

    }else {
        /*the player's x coordinate is reset back to the middle, and their velocity is reset to 0*/
        stage.y = ph / 2;
        /*the player's artificialY needs to be reset to whichever stage they reached, each time they respawn*/
        /*instead of checking each id of the stage and manually setting the player's artificial y coordinate, it is instead taken from the information provided by the database*/
        for (let stageInfo of stagesInfo) {
            if (stage.stageid == stageInfo[0]) player.artificialY = stageInfo[2];
        }
        player.x = pw / 2;
        player.dx = 0;
    }
}

function saveKills(){
    for(let kill of kills){
        let formData = new FormData();
        /*the form data is filled with the monsterid and the sessionkills*/
        formData.append("monsterid", kill.monsterid.toString());
        formData.append("sessionKills", kill.sessionKills.toString());

        fetch('/kills/update/', {method: 'post', body: formData}
        ).then(response => response.json()
        ).then(responseData => {
            if (responseData.hasOwnProperty('error')) alert(responseData.error);
        });
    }
}

function monsterDeath(monster){
    /*the this boolean variable will turn true if the kill exists*/
    let killExists = false;
    for (let kill of kills){
        /*if one of the kills already has one of the ids of the monster, then it exists*/
        if(kill.monsterid === monster.monsterid){
            killExists = true;
            /*the session kills for that specific monster is incremented*/
            kill.sessionKills ++;
        }
    }
    /*if the kill doesn't exist yet, then a new kill is pushed*/
    if(killExists === false) {
        kills.push(new Kill(monster.monsterid))
    }
    /*the player's score is increased by the monster's value multiplied by the current stage they are on*/
    /*i.e. if the player kills a monster with a value of 200 on stage 2, they would gain 400 score*/
    player.score += monster.value;
    monster.alive = false;
}
function saveHighScore(){
    let formData = new FormData();
    formData.append("newHighScore", (player.highScore).toString());

    fetch('/players/updateHighScore/', {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(responseData => {
        /*to add extra validation, an alert will display if there is an error*/
        if (responseData.hasOwnProperty('error')) alert(responseData.error);
    });
}
function starCollect(star){
    /*the player's score is increased by 500 (the star's value)*/
    player.score += star.value;
    /*the player's currency is increased by 5 (0.01*500)*/
    player.currency += star.value*0.01;
    /*the cumulative currency goes up the same amount as the currency*/
    player.cumCurrency += star.value*0.01;
    /*if the player collects the star it is no longer active, and will disappear*/
    star.active = false;
}
/*this works pretty much the same as the other save functions*/
function saveCurrency(){
    let formData = new FormData();
    formData.append("sessionCurrency", (player.currency).toString());

    fetch('/players/updateCurrency/', {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(responseData => {
        /*to add extra validation, an alert will display if there is an error*/
        if (responseData.hasOwnProperty('error')) alert(responseData.error);
    });
}
function processes(frameLength){
    /*if the player's score is larger than the high score, then the high score will become the player's score*/
    /*because processes is run many times per second, to the player it will look like they are updating at the same time*/
    if(player.highScore === null) player.highScore = 0;
    if(player.score> player.highScore) player.highScore = player.score;

    /*the setImage method is used instead of manually checking the stageid*/
    stage.setImage();
    /*the player is given a very short amount of time, between when the timer is 1 and 0.75 (equating to around 15 frames) where they are attacking and can kill an enemy*/
    if(player.cooldownTimer > 0.5){
        player.attacking = true;
    }else{
        player.attacking = false;
    }

    for(let i = 1; i <5; i++){
        if (player.skinid == i) for (let star of stars) star.spriteFrame = i-1;
    }

    for(let i = 0; i < 12; i++) {
        if (player.cooldownTimer < (12-i)/12 && player.cooldownTimer > (11-i)/12) player.spriteFrame = i;
    }

    /*once the cooldown reaches 0 then the cooldown is set to false because it has finished*/
    /*since the cooldown is false again, the spacebar will be registered if the player presses it, which will in turn trigger the cooldown*/
    if(player.cooldownTimer <= 0){
        /*the cooldown timer needs to be reset to 0 so that it doesn't become negative*/
        /*otherwise, once the cooldown reached 0, then a couple more frame lengths could be taken away, making the timer negative*/
        player.cooldownTimer = 0;
        player.cooldown = false;
    }
    for (let projectile of projectiles){
        projectile.update(frameLength);
        if( projectile.y < -10) projectile.active = false;

        /*the player has a height of 64, so their radius is 32*/
        /*the projectile has a height of 10 so its radius is 5*/
        /*the total distance for them to be touching is 37, but since I have given the monster collision detection a leniency of 5 pixels, then the total distance is 32*/
        /*therefore, the separation needs to only be 32 - half the player's height*/
        if (separation(projectile, player) < player.image.height/2) {
            projectile.active = false;
            playerDeath();
        }
        if (!player.alive) projectile.active = false;
    }
    projectiles = projectiles.filter(p => p.active);

    for( let monster of monsters){
        monster.update(frameLength);

        /*if the monster's y coordinate is higher than -64 (the monster's height), then the monster is no longer alive*/
        if( monster.y < -(monster.image.height)) monster.alive = false;



        /*if the distance between the current monster and the player is smaller than the height of the monster-2 then the resolveCollision function is run*/
        /*the reason why 5 is taken away, is so that a tiny bit of overlap is allowed, before it is registered as a collision*/
        if (separation(monster, player) < monster.image.height-5) playerDeath();

        /*the player must be above the monster to be able to attack them*/
        if(player.y < monster.y){
            /*for the attack to register, the distance between the monster and the player must be less than 80 pixels (monster height *2)*/
            /*the player must be attacking for the hit to register*/
            if((separation(monster, player) < monster.image.height+5) && player.attacking === true) monsterDeath(monster);

        }

        /*if the player is not alive then the monster is also not alive*/
        if (!player.alive) monster.alive = false;
    }
    /*the monsters array is filtered*/
    /*this means a new array is created, with only the monsters that are still alive*/
    monsters = monsters.filter(m => m.alive);


    stage.update(frameLength);
    player.update(frameLength);


    for (let star of stars){
        star.update(frameLength);
        if( star.y < -(star.image.height)) star.active = false;
        /*this is very similar to the collision detection between the player and the monster, with 5 pixels of overlap*/
        if (separation(star, player) < star.image.height-5) starCollect(star);
        /*if the star moves off the screen, or if the player is dead, then it is no longer active, similar to the monsters*/
        if(!player.alive) star.active = false;
    }
    /*all the stars that are no longer active will be filtered out of the stars array*/
    stars = stars.filter(s => s.active);

    for(let death of deaths){
        death.update(frameLength);
        if (!player.alive) death.active = false;
        /*if the death goes of the screen, it is no longer active*/
        if( death.y < -20) death.active = false;
    }
    deaths = deaths.filter(d => d.active);

}

/*this will change each time the player dies, so it needs to be a variable not a constant*/

/*the playable area is an offscreen canvas, and is rendered offscreen using the pre-defined width and height*/
const playableArea = new OffscreenCanvas(pw, ph);

function outputs(){
    /*the context for the playable area is set as a constant called 'pac' (playable area canvas)*/
    const pac = playableArea.getContext('2d');
    /*I have finally changed the base colour of the playable area to be the same hot pink used throughout the rest of the website*/
    pac.fillStyle = "#c0006f";
    pac.fillRect(0,0, pw, ph);

    stage.draw(pac);
    /*the player is drawn, passing in the 'pac' as the context*/
    player.draw(pac);

    for(let monster of monsters){
        monster.draw(pac);
    }

    for (let star of stars){
        star.draw(pac);
    }

    for(let death of deaths){
        death.draw(pac);
    }
    for (let projectile of projectiles){
        projectile.draw(pac);
    }

    /*the player's artificial Y will only be exactly 0 when they first load in, and for a frame when they respawn or die, however the latter will not affect the screen because it is so miniscule of a time frame*/
    if(!player.alive && player.artificialY === 0){
        /*the text will be white*/
        pac.fillStyle = "white";
        /*the text must be aligned to the center just like in css, otherwise it will be drawn from the top left*/
        pac.textAlign = "center";
        /*the game over part will be 100 pixels big to make it it clear to the player*/
        pac.font = "100px squarewave-bold";
        pac.font = "50px squarewave-bold";
        pac.fillText("A and D: move", pw/2, ph/2-50);
        pac.fillText("SPACE: attack", pw/2, ph/2-10);
        /*the instructions for the player to restart will be below the game over text, in a smaller font*/
        pac.fillText("press ENTER to start", pw/2, ph/2+50);
    }else if(!player.alive){
        /*the text will be white*/
        pac.fillStyle = "white";
        /*the text must be aligned to the center just like in css, otherwise it will be drawn from the top left*/
        pac.textAlign = "center";
        /*the game over part will be 100 pixels big to make it it clear to the player*/
        pac.font = "100px squarewave-bold";
        pac.fillText("GAME OVER", pw/2, ph/2-20);
        /*the instructions for the player to restart will be below the game over text, in a smaller font*/
        pac.font = "50px squarewave-bold";
        pac.fillText("press ENTER to restart", pw/2, ph/2+50);
    }
    /*the game canvas is 'gc'*/
    const gameCanvas = document.getElementById('gameCanvas');
    const gc = gameCanvas.getContext('2d');

    /*I have filled the game canvas with blue*/
    gc.fillStyle = "black";
    gc.fillRect(0, 0, gw, gh);

    /*gc.drawImage(image, dx, dy)*/
    /*the playableArea is the 'image' that is placed in the middle of the game canvas*/
    /*dx and dy are the x and y coordinates in the destination canvas at which to place the top-left corner of the source image*/
    /*half of the width and height of the playable area is taken away from half the width and the height of the game canvas to work out the dx and dy*/
    gc.drawImage(playableArea,gw/2 - pw/2, gh/2 - ph/2);

    if(player.artificialY !=0){
        gc.fillStyle = "white";
        gc.font = "35px squarewave-bold";
        /*the score and high score are in top left corner*/
        /*the y offset is larger than the x offset because font size of the number being displayed is larger than the size of the characters so it needs to be accounted for*/
        gc.fillText("high score: " + player.highScore, 20, 30)
        gc.fillText("score: " + player.score, 20, 60);
        /*the x offset is the width of the whole game - the playable area, which gets you to the right edge of the playable area +20 so it's the same as the left side*/
        gc.fillText("currency: " + player.cumCurrency, gw-pw/2+20, 30);
        gc.fillText("lives: " + player.lives, gw-pw/2+20, 60);
        /*the player x and y need to be rounded so that they are not insanely large decimal numbers on the screen*/
        /*this text is in the bottom left corner*/
        gc.fillText("x: " + Math.round(player.x) + ", y: " + Math.round(player.artificialY), 20, gh-20);
        gc.fillText("stage: " + stage.stageid, gw-pw/2+20, gh-20);
    }

}