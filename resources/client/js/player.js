/*the player variable is created, and will get assigned a value later*/
let player;
/*the playerImage is a new image object */
let playerImage = new Image();

/*loadPlayerImage is assigned a promise object*/
let loadPlayerImage = new Promise(function(resolve) {
    /*the source of the image is assigned*/
    /*for now, there is just one image, as the skins have not yet been designed*/
    playerImage.src = "/client/img/player.png";
    /*the promise is resolved once the image loads*/
    playerImage.onload = () => resolve();
});

class Player{
    /*a constructor creates the player object*/
    constructor(){
        /*the player's x and y coordinates are in the middle of the playable area*/
        this.x = pw/2;
        this.y = ph/2;

        this.artificialY = 0;

        /*dx is the horizontal velocity of the player and starts off as 0 because the player is not moving left and right when the game loads*/
        this.dx = 0;
        this.dy = 500;

        /*the player's skin is the playerImage object that as declared earlier*/
        this.skin = playerImage;
        /*the player starts off as alive, and with 3 lives*/
        this.alive = true;
        this.lives = 3;

    }
    draw(context){
        /*if the player is dead, they are not drawn*/
        if(!player.alive) return;
        /*the player is drawn in the middle of the playable area*/
        /*half the width and the height are taken away because the drawImage function draws from the corner*/
        context.drawImage(this.skin, this.x - this.skin.width/2, this.y - this.skin.height/2);
    }
    update(frameLength) {
        /*if the player is dead, they are not updated*/
        if (!player.alive) return;
        /*the x coordinate is increased or decreased depending on whether the velocity is positive or negative*/
        this.x += frameLength * this.dx;
        this.artificialY += frameLength * this.dy;

        /*if the x coordinate is smaller than half the player's width, then they the x coordinate is reset to that same value*/
        if (this.x < this.skin.width/2) {
            this.x = this.skin.width/2;
            /*the velocity is set to 0 because technically the player is stopping*/
            this.dx = 0;
        }
        /*the same happens on the other side of the playable area*/
        if (this.x > pw - this.skin.width/2) {
            this.x = pw - this.skin.width/2;
            this.dx = 0;
        }

    }
}