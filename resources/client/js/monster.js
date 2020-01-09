/*const monsterImageCount = 1;*/
const monsterInfo = [];

/*because there is multiple monsters, the monsters are stored as an array*/
let monsters = [];

/*let loadMonsterImages = new Promise(function(resolve) {

    let loadedImageCount = 0;

    let loadCheck = function() {
        loadedImageCount++;
        if (loadedImageCount === monsterImageCount) {
            resolve();
        }
    };

    for (let i = 1; i <= 2; i++) {
        let img = new Image();
        img.src = "/client/img/monster" + i + ".png";
        img.onload = () => loadCheck();
        monsterImages.push(img);
    }

});*/

let loadMonsterInfo = new Promise(function(resolve) {
    let loadedImageCount = 0;
    /*the loadCheck takes in the parameter of the skins that are returned in the form of the json response*/
    let loadCheck = function(monstersDb) {
        loadedImageCount++;
        /*rather than hard coding the amount of images that are meant to load, the length of the json response is used instead*/
        if (loadedImageCount === monstersDb.length) {
            resolve();
        }
    };
    fetch('/monsters/list' , {method: 'get'}
    ).then(response => response.json()
    ).then(monstersDb => {
        for (let monsterDb of monstersDb) {
            console.log(monsterDb.imageFile);
            console.log(monsterDb.monsterid);
            console.log(monsterDb.movementType);
            console.log(monsterDb.attackType);
            console.log(monsterDb.stageid);
            let img = new Image();
            img.src = monsterDb.imageFile;

            img.onload = () => loadCheck(monstersDb);

            monsterInfo.push([monsterDb.monsterid, img, monsterDb.movementType, monsterDb.attackType, monsterDb.stageid]);
        }
    });
});

class Monster{
    constructor(monsterid, image, movementType, attackType, stageid, x){
        this.monsterid = monsterid;
        this.image = image;
        this.movementType = movementType;
        this.attackType = attackType;
        this.stageid = stageid;

        /*the starting x coordinate will be passed in as a parameter*/
        this.x = x;
        /*the starting y coordinate is half of the image's height below the canvas*/
        this.y = ph + this.image.height/2;
        this.dx = 0;
        /*the monsters have a negative velocity, so they are moving up, which makes the player look like they are falling*/
        this.dy = -300;

        this.alive = true;
        /*this is the value of the monster, and if the player kills the monster then they will gain this amount of score*/
        this.value = 100;

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

