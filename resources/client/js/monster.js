const monsterImageCount = 1;
const monsterImages = [];

/*because there is multiple monsters, the monsters are stored as an array*/
let monsters = [];

let loadMonsterImages = new Promise(function(resolve) {

    let loadedImageCount = 0;

    let loadCheck = function() {
        loadedImageCount++;
        if (loadedImageCount === monsterImageCount) {
            resolve();
        }
    };

    for (let n = 1; n <= 2; n++) {
        let img = new Image();
        img.src = "/client/img/monster" + n + ".png";
        img.onload = () => loadCheck();
        monsterImages.push(img);
    }

});

class Monster{
    constructor(id, x){
        this.type = id;

        this.image = monsterImages[id];
        /*the starting x coordinate will be passed in as a parameter*/
        this.x = x;
        /*the starting y coordinate is half of the image's height below the canvas*/
        this.y = ph + this.image.height/2;
        this.dx = 0;
        /*the monsters have a negative velocity, so they are moving up, which makes the player look like they are falling*/
        this.dy = -300;

        this.alive = true;

    }

    draw(context){
        if (!this.alive) return;
        context.drawImage(this.image, this.x - this.image.width/2, this.y- this.image.height/2);
    }

    update(frameLength) {
        if (!this.alive) return;

        this.x += frameLength * this.dx;
        this.y += frameLength * this.dy;

    }
}

