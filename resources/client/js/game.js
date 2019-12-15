let gw, gh, pw = 600, ph = 600;
let lastTimestamp = 0;

const pressedKeys = {};

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

            prepareMonsters();

            window.requestAnimationFrame(gameFrame);
        });
    });

}



function prepareMonsters() {

    for (let i = 0; i < 20; i++) {

        let randomStartX = Math.floor(Math.random() * (pw - 64)) + 32;
        let randomSpawnDelay = Math.random() * 15;
        monsters.push(new Monster(1, randomStartX, randomSpawnDelay));

    }

}

let frame = 0;
function gameFrame(timestamp) {

    console.log(frame++);


    if (lastTimestamp === 0) lastTimestamp = timestamp;
    const frameLength = (timestamp - lastTimestamp) / 1000;
    lastTimestamp = timestamp;

    console.log(frameLength);
    inputs(frameLength);
    processes(frameLength);
    outputs();

    window.requestAnimationFrame(gameFrame);
}
function processes(frameLength){

    for( let monster of monsters){
        monster.update(frameLength);
    }
    monsters = monsters.filter(m => m.alive);

    player.update(frameLength);

}
function inputs(frameLength){
    if (player.alive){
        if(pressedKeys["a"]){
            console.log("a");
            player.dx -= 20000*frameLength;
            if (player.dx < -600) player.dx = -600;
        }else if(pressedKeys["d"]){
            console.log("d");
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
    pac.fillStyle = 'blue';
    pac.fillRect(0,0, pw, ph);


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
