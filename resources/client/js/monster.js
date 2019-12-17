const monsterImageCount = 1;
const monsterImages = [];

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
    constructor(id, spawnX, spawnDelay){
        this.type = id;

        this.image = monsterImages[id];

        this.x = spawnX;
        this.y = ph+ this.image.width/2;
        this.dx = 0;
        this.dy = -300;

        this.alive = true;

        this.spawnDelay = spawnDelay;

    }
    restart(spawnX, spawnDelay){
        this.x = spawnX;
        this.y = ph + this.image.width/2;

        this.alive = true;

        this.spawnDelay = spawnDelay;
    }

    draw(context){
        if (!this.alive) return;
        context.drawImage(this.image, this.x - this.image.width/2, this.y- this.image.width/2);
    }

    update(frameLength) {
        if (!this.alive) return;

        if (this.spawnDelay > 0) {

            this.spawnDelay -= frameLength;

        }else {
            this.x += frameLength * this.dx;
            this.y += frameLength * this.dy;
        }
    }
}