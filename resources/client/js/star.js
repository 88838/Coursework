/*like monsters, there are many stars, so they need to be stored in an array*/
let stars = [];

/*there is only one image for the star so no arrays are needed*/
let starImage = new Image();
let loadStarImage = new Promise(function(resolve) {
    starImage.src = "/client/img/star.png";
    starImage.onload = () => resolve();
});

class Star{
    constructor(x){
        this.image = starImage;
        /*the starting x coordinate will be passed in, the same as the monster constructor*/
        this.x = x;
        /*the starting y coordinate is below the canvas, like the monsters*/
        this.y = ph + this.image.height/2;
        /*the vertical velocity is slightly higher than the monster so that the stars are harder to catch*/
        this.dy = -450;
        /*since the star is technically not living, I have changed the keyword to active, however it will work the same as the alive attribute in the monster and player objects*/
        this.active = true;
        /*the star will give the player score, but will also give them currency, which will calculated later*/
        this.value = 500;
        this.spriteFrame = 0;
    }

    draw(context){
        if (!this.active) return;
        context.drawImage(this.image, this.spriteFrame *  64, 0, 64, 64, this.x - 32, this.y - 32, 64, 64);
    }

    update(frameLength) {
        if (!this.active) return;
        this.y += frameLength * this.dy;
    }
}