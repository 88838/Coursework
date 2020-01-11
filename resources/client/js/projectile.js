let projectiles = [];

class Projectile{
    constructor(x, y){
        this.x = x;
        this.y = y;
        /*the velocity of the projectile must be much faster than the monster*/
        this.dy = -600;
        this.active = true;
    }

    draw(context){
        if (!this.active) return;
        context.fillStyle = 'white';
        context.beginPath();
        context.rect(this.x - 5, this.y - 5, 10, 10);
        context.fill();
    }
    update(frameLength){
        if (!this.active) return;
        this.y += frameLength * this.dy;
    }
}