const monsterImageCount = 2;
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
    constructor(id, x){
        this.type = id;
        this.startX = x;

        this.image = monsterImages[id];

/*        this.x = 0;*/
        this.y = ph;
        this.dx = 0;
        this.dy = -100;

        this.alive = true;
        this.spawnTimer=0;

    }
    draw(context){
        if (!this.alive) return;
        context.drawImage(this.image, this.startX - this.image.width/2, this.y - this.image.height/2);
    }

    update(frameLength) {

        if (!this.alive) return;

        if (this.spawnTimer > 0) {

            this.spawnTimer -= frameLength;

        }else {
            this.x += frameLength * this.dx;
            this.y += frameLength * this.dy;
        }
    }
}