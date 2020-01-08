let gw = 1200, gh = 600, pw = 600, ph = 600;
let lastTimestamp = 0;

/*the keys that are pressed are stored as an object*/
const pressedKeys = {};

function pageLoad(){
    document.getElementById("mainMenuOption").addEventListener("click", ()=> window.location.href = "/client/index.html");

    /*if the key is pressed, it is set to true for that key*/
    /*once the key has been let go, it is set as false*/
    window.addEventListener("keydown", event => pressedKeys[event.key] = true);
    window.addEventListener("keyup", event => pressedKeys[event.key] = false);

    /*only once the image has loaded, is the first frame requested*/
    /*have to now load stage as well*/
    loadStageImage.then(() =>{
        loadPlayerImage.then(() => {
            loadMonsterImages.then(() => {
                /*the star image now also needs to be loaded*/
                loadStarImage.then(() => {
                    player = new Player();
                    stage = new Stage(1);

                    /*if the player is alive, a new monster is pushed every 450 milliseconds*/
                    setInterval(() => {if (player.alive) monsters.push(new Monster(1, randomX()))}, 450);
                    /*one star will show up every 5 seconds*/
                    setInterval(() => {if (player.alive) stars.push(new Star( randomX()))}, 5000);
                    /*the gameFrame function has to be requested for the first time in when the page loads*/
                    window.requestAnimationFrame(gameFrame);
                });
            });
        });
    });

    /*if the player is alive, their score is increased by 1 every 100 milliseconds (by 10 every second)*/
    /*this is ongoing, so that even if the player doesn't kill any monsters or collects any currency, it still adds score for as long as they survive*/
    setInterval(() => {if (player.alive) player.score+=1}, 100);
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
        /*if the "a" is true (has been pressed)*/
        if(pressedKeys["a"]){
            /*the player accelerates at a certain rate*/
            player.dx -= 10000*frameLength;
            /*the player's max velocity is 600 and -600*/
            if (player.dx < -700) player.dx = -700;
        }else if(pressedKeys["d"]){
            player.dx += 10000*frameLength;
            if (player.dx > 700) player.dx = 700;
        } else{
            /*when no keys are pressed, the player decelerates at a certain rate*/
            /*the velocity is multiplied by a frameLength, which is a number less than 1, so it will decrease each frame until it reaches 0*/
            player.dx *= frameLength;
        }
        /*if the player presses the space bar, then they are attacking*/
        if(pressedKeys[" "]){
            player.attacking = true;
        }else{
            player.attacking = false;
        }
    }
}

function separation(entity1, entity2) {
    /*this is the javascript version of the separation function created in Phase 3 Design*/
    /*the Math object is used to work out the square root and power of different numbers*/
    return Math.sqrt(Math.pow(entity1.x - entity2.x, 2) /* the 2 is the power*/
        + Math.pow(entity1.y - entity2.y, 2));
}

function randomX(){
    return Math.floor(Math.random() * (pw - 64)) + 32;
}

function playerRespawn(){
    /*all the monsters are killed*/
    for (let monster of monsters) monster.alive = false;
    for (let star of stars) star.active = false;
    /*the player's x coordinate is reset back to the middle, and their velocity is reset to 0*/
    stage.y = ph/2;
    /*the player's artificialY needs to be reset to whichever stage they reached, each time they respawn*/
    if(stage.id === 3){
        player.artificialY = 50000;
    }else if(stage.id === 2){
        player.artificialY=10000;
    }else{
        player.artificialY=0
    }
    player.x = pw/2;
    player.dx = 0;
    /*the player loses a life*/
    player.lives -=1;
}

function monsterDeath(monster){
    /*the player's score is increased by the monster's value multiplied by the current stage they are on*/
    /*i.e. if the player kills a monster with a value of 200 on stage 2, they would gain 400 score*/
    player.score += monster.value*stage.id;
    monster.alive = false;
}

function processes(frameLength){
    /* console.log(player.lives);*/
    /*this if statement has to be done backwards, with stage 3 being first*/
    /*if it was the other way around, then once the player has reached stage 2, the if statement wouldn't carry on*/

    if(player.lives === 0) player.alive = false;

    if(player.artificialY >= 50000){
        stage.id = 3;
    } else if(player.artificialY >= 10000){
        stage.id = 2;
    }

    for( let monster of monsters){
        monster.update(frameLength);

        /*if the monster's y coordinate is higher than -64 (the monster's height), then the monster is no longer alive*/
        if( monster.y < -(monster.image.height)){
            monster.alive = false;
        }

        /*the player must be above the monster to be able to attack them*/
        if(player.y < monster.y - player.image.height){
            /*for the attack to register, the distance between the monster and the player must be less than 80 pixels (monster height *2)*/
            /*the player must be attacking for the hit to register*/
            if((separation(monster, player) < monster.image.height*1.25) && player.attacking === true) monsterDeath(monster);

        }

        /*if the distance between the current monster and the player is smaller than the height of the monster-2 then the resolveCollision function is run*/
        /*the reason why 2 is taken away, is so that a tiny bit of overlap is allowed, before it is registered as a collision*/
        if (separation(monster, player) < monster.image.height-2) {
            /*if the player has 1 life left then they die the next time they get it, otherwise they respawn*/
            if (player.lives === 1){
                player.alive = false;
            }else{
                playerRespawn();
            }
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
        if (separation(star, player) < star.image.height-2){
            player.score += star.value;
            star.active = false;
        }
        if( star.y < -(star.image.height)) star.active = false;
        if(!player.alive) star.active = false;
    }
    stars = stars.filter(s => s.active);
}

/*the playable area is an offscreen canvas, and is rendered offscreen using the pre-defined width and height*/
const playableArea = new OffscreenCanvas(pw, ph);

function outputs(){
    /*the context for the playable area is set as a constant called 'pac' (playable area canvas)*/
    const pac = playableArea.getContext('2d');
    /*to check the canvas is working, I have filled the playable area with red*/
    pac.fillStyle = "blue";
    pac.fillRect(0,0, pw, ph);

    console.log(player.score);

    stage.draw(pac);
    /*the player is drawn, passing in the 'pac' as the context*/
    player.draw(pac);
    for(let monster of monsters){
        monster.draw(pac);
    }

    for (let star of stars){
        star.draw(pac);
    }


    // ------------------
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

}