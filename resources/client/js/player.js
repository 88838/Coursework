let player;
let playerImage = new Image();

let loadPlayerImage = new Promise(function(resolve) {
    playerImage.src = "/client/img/player.png";
    playerImage.onload = () => resolve();
});

class Player{
    constructor(x, y){
        this.x = x;
        this.dx = 0;
        this.y = y;
        this.dy = 0;
        this.alive = true;
        this.lives = 3;
        this.reloadTimer = 1;
    }
    draw(context){
        if(!player.alive) return;
        context.drawImage(playerImage, this.x - playerImage.width/2, this.y - playerImage.height/2);

    }
    update(frameLength) {
        if (!player.alive) return;

        this.x += frameLength * this.dx;
        this.y += frameLength * this.dy;

        if (this.x < playerImage.width/2) {
            this.x = playerImage.width/2;
            this.dx = 0;
        }
        if (this.x > pw - playerImage.width/2) {
            this.x = pw - playerImage.width/2;
            this.dx = 0;
        }
        if (this.y < playerImage.height/2) {
            this.y = playerImage.height/2;
            this.dy = 0;
        }
        if (this.y > ph - playerImage.height/2) {
            this.y = ph - playerImage.height/2;
            this.dy = 0;
        }

    }
}