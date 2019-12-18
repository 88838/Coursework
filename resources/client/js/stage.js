let stage;
const background = new Image();
background.src="/client/img/background.jpg";

class Stage{
    constructor(id) {
        this.image = background;
        this.type = id;
        this.y = 0;
        this.dy = 500;
    }
    restart(){
        this.y = 0;
    }
    draw(context){
        if (!player.alive) return;
        /*console.log(this.y);
        console.log(this.image.height);*/
        if(this.y >= this.image.height-800) this.y = 0;
        context.drawImage(this.image, 0, this.y, 600, 600, 0, 0, 600, 600);
    }

    update(frameLength) {
        if (!player.alive) return;
        this.y += frameLength * this.dy;

    }
}