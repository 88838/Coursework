let gw, gh, pw = 600, ph = 600;
let lastTimestamp = 0;

const pressedKeys = {};


function separation(entity1, entity2) {

    return Math.sqrt(Math.pow(entity1.x - entity2.x, 2)
        + Math.pow(entity1.y - entity2.y, 2));
}

function randomX(){
    return Math.floor(Math.random() * (pw - 64)) + 32;
}
let score = 0;
function pageLoad(){
    document.getElementById("mainMenuOption").addEventListener("click", ()=> window.location.href = "/client/index.html");

    const canvas = document.getElementById('gameCanvas');
    gw = canvas.width;
    gh = canvas.height;

    window.addEventListener("keydown", event => pressedKeys[event.key] = true);
    window.addEventListener("keyup", event => pressedKeys[event.key] = false);

    loadPlayerImage.then(() => {
        loadMonsterImages.then(() => {
            player = new Player(pw/2, ph/2);
            stage = new Stage(1);

            setInterval(() => {monsters.push(new Monster(1, randomX()))}, 450);
            setInterval(()=> {if(player.lives >=0) score ++}, 150);
            //setInterval(() => {monsters.push(new Monster(2, randomX()))}, 2000);

            window.requestAnimationFrame(gameFrame);
        });
    });
}

function gameFrame(timestamp) {

    if (lastTimestamp === 0) lastTimestamp = timestamp;
    const frameLength = (timestamp - lastTimestamp) / 1000;
    lastTimestamp = timestamp;
    inputs(frameLength);
    processes(frameLength);
    outputs();

    window.requestAnimationFrame(gameFrame);
}


function resolveCollision(){
    for (let monster of monsters) monster.alive = false;
    stage.restart();
    player.lives -=1;
}


function processes(frameLength){
    console.log(player.lives);
    for( let monster of monsters){
        monster.update(frameLength);

        if( monster.y < -(monster.image.height*2)){
            monster.alive = false;
        }

        if (separation(monster, player) < monster.image.height-2) {
            resolveCollision();
        }

        if (!player.alive) monster.alive = false;
    }

    console.log(score);
    monsters = monsters.filter(m => m.alive);
    stage.update(frameLength);
    player.update(frameLength);

}
function inputs(frameLength){
    if (player.alive){
        if(pressedKeys["a"]){
            player.dx -= 20000*frameLength;
            if (player.dx < -600) player.dx = -600;
        }else if(pressedKeys["d"]){
            player.dx += 20000*frameLength;
            if (player.dx > 600) player.dx = 600;
        } else{
            player.dx *= 1 - 75 * frameLength;
        }
    }
}

const playableArea = new OffscreenCanvas(pw, ph);

function outputs(){

    const pac = playableArea.getContext('2d');
    pac.fillStyle = 'red';
    pac.fillRect(0,0, pw, ph);

    stage.draw(pac);


    player.draw(pac);
    for(let monster of monsters){
        monster.draw(pac);
    }


    // ------------------

    const gameCanvas = document.getElementById('gameCanvas');
    const gc = gameCanvas.getContext('2d');

    gc.fillStyle = "black";
    gc.fillRect(0, 0, gw, gh);

    gc.drawImage(playableArea,gw/2 - pw/2, gh/2 - ph/2);

}