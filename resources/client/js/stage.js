let stage;
let stageImage = new Image();


let loadStageImage = new Promise(function(resolve) {
    stageImage.src="/client/img/stage.png";
    stageImage.onload = () => resolve();
});

class Stage{
    constructor(id) {
        this.image = stageImage;
        this.type = id;
        /*the starting y coordinate is in the middle of the canvas*/
        this.y = ph/2;
        /*the stage is scrolling upwards, so it has a negative velocity*/
        this.dy = -500;
    }

    draw(context){
        if (!player.alive) return;
        /*the first image is drawn in the centre*/
        context.drawImage(this.image, 0, this.y - this.image.height/2);
        /*the second image is offset by the full image height below the first image*/
        context.drawImage(this.image, 0, this.y + this.image.height/2);
    }

    update(frameLength) {
        if (!player.alive) return;
        /*if the y coordinate travels 600 pixels upwards (the whole height of the image), then the y coordinate is reset*/
        if(this.y <= (-this.image.height/2)) this.y = ph/2;
        this.y += frameLength * this.dy;

    }
}