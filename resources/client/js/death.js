let deaths = [];
class Death{
    constructor(livesLeft, x, y){
        this.x = x;
        this.y = y;
        this.livesLeft = livesLeft;
        /*like the player, the death has an artificial y coordinate, which will change so that it can be animated*/
        /*the actual y coordinate will not change so it can be used to display to the user where they died*/
        this.artificialY = y;
        /*same velocity as the monster*/
        this.dy = -350;
        this.active = true;
    }
    draw(context){
        if (!this.active) return;
        /*the text will 20px in height and the same font as the rest of the page*/
        context.font = "20px squarewave-bold";
        context.fillStyle = "white";
        context.textAlign = "center";
        /*the opacity of the image and the text is half*/
        context.globalAlpha = 0.5;
        /*the player image is drawn ph/4 (150px) above the death's x and y coordinates, to account for the player's offset from the top of the playable area*/
        context.drawImage(player.image, this.x - player.image.width/2, this.artificialY - player.image.height/2 + ph/4);
        /*the text is drawn below the player image (x is 3/4 of the image below, and y is the full height below)*/
        context.fillText("x: " + this.x, this.x, this.artificialY + ph/4 + 3*player.image.height/4);
        context.fillText("y: " + this.y, this.x, this.artificialY + ph/4 + player.image.height);
/*        context.fillText("lives left: " + this.livesLeft, this.x, this.artificialY + ph/4 + 5*player.image.height/4);*/
        /*the globalAlpha has to be reset so that the rest of the game is drawn at full opacity*/
        context.globalAlpha = 1;
    }
    /*same as the other update methods*/
    update(frameLength){
        if (!this.active) return;
        this.artificialY += frameLength * this.dy;
    }
}