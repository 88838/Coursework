/*the monsterImages array is now monster, as it will now store all the information about the monsters from the database*/
const monstersInfo = [];

/*because there is multiple monsters, the monsters are stored as an array*/
let monsters = [];

let loadMonstersInfo = new Promise(function(resolve) {
    let loadedImageCount = 0;
    let loadCheck = function(monstersDb) {
        loadedImageCount++;
        if (loadedImageCount === monstersDb.length) {
            resolve();
        }
    };
    fetch('/monsters/list' , {method: 'get'}
    ).then(response => response.json()
    ).then(monstersDb => {
        if (monstersDb.hasOwnProperty('error')) alert(monstersDb.error);
        for (let monsterDb of monstersDb) {
            let image = new Image();
            image.src = monsterDb.imageFile;

            image.onload = () => loadCheck(monstersDb);
            /*the monsterInfo array is pushed in the same way the skinImages were pushed*/
            /*this time, all of the attributes from the database apart from the name are pushed instead of just the id and the file name*/
            monstersInfo.push([monsterDb.monsterid, image, monsterDb.movementType, monsterDb.attackType, monsterDb.stageid]);
        }
    });
});

class Monster{
    /*all of the attributes from the database need to be passed into the constructor*/
    constructor(monsterid, image, attackType, stageid, x, dx){
        this.monsterid = monsterid;
        this.image = image;
        this.attackType = attackType;
        this.stageid = stageid;

        /*the starting x coordinate will be passed in as a parameter*/
        this.x = x;
        /*the starting y coordinate is half of the image's height below the canvas*/
        this.y = ph + this.image.height/2;
        this.dx = dx;
        /*the monsters have a negative velocity, so they are moving up, which makes the player look like they are falling*/
        this.dy = -350;

        this.alive = true;
        /*this is the value of the monster, and if the player kills the monster then they will gain this amount of score*/
        this.value = this.monsterid*100;

    }

    draw(context){
        if (!this.alive) return;
        context.drawImage(this.image, this.x - this.image.width/2, this.y- this.image.height/2);
    }

    update(frameLength) {
        if (!this.alive) return;

        this.x += frameLength * this.dx;
        this.y += frameLength * this.dy;

        if (this.x < this.image.width/2) {
            this.x = this.image.width/2;
            /*the velocity is set to the negative of the current velocity, causing them to chang direction and essentially bounce off the 'wall'*/
            this.dx = -this.dx;
        }
        /*the same happens on the other side of the playable area*/
        if (this.x > pw - this.image.width/2) {
            this.x = pw - this.image.width/2;
            this.dx = -this.dx;
        }
    }
}

