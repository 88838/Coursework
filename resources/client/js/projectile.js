/*there are multiple projectiles so they must be stored in an array*/
let projectiles = [];

class Projectile{
    /*the monster's x and y coordinates need to be passed in as parameters*/
    constructor(x, y){
        this.x = x;
        this.y = y;
        /*the velocity of the projectile must be much faster than the monster*/
        this.dy = -600;
        this.active = true;
    }

    draw(context){
        if (!this.active) return;
        /*the projectile will be a white square*/
        context.fillStyle = 'white';
        context.beginPath();
        /*the drawing of a rectangle works the same as images, from the top left corner*/
        /*the width and height is 10, so half the height needs to be taken away to get to the top left corner*/
        context.rect(this.x - 5, this.y-20, 10, 10);
        context.fill();
    }
    /*same as the other update methods*/
    update(frameLength){
        if (!this.active) return;
        this.y += frameLength * this.dy;
    }
}