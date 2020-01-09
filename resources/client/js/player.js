/*the player variable is created, and will get assigned a value later*/
let player;
/*the playerImage is a new image object */
let playerImage = new Image();

/*loadPlayerImage is assigned a promise object*/
let loadPlayerImage = new Promise(function(resolve) {
    /*the source of the image is assigned*/
    /*for now, there is just one image, as the skins have not yet been designed*/
    playerImage.src = "/client/img/player.png";
    /*the promise is resolved once the image loads*/
    playerImage.onload = () => resolve();
});

let loadPlayerImages = new Promise(function(resolve) {
    let loadedImageCount = 0;

    let loadCheck = function() {
        loadedImageCount++;
        if (loadedImageCount === monsterImageCount) {
            resolve();
        }
    };
    fetch('/skins/list' , {method: 'get'}
    ).then(response => response.json()
        /*this must be called playerDb as to not get confused with the player object that is being used in the game*/
    ).then(skinsDb => {
        for (let skinDb of skinsDb) {
            let img = new Image();
            img.src = skinDb.imageFile;
            img.onload = () => loadCheck();
            skinImages.push(img, skinsDb.skinid);
        }
    });

});

/*this promise function works in the same way as the ones which are used to load the images of the monster, stage and player*/
let createPlayer = new Promise(function(resolve) {
    fetch('/players/get/' + Cookies.get("token"), {method: 'get'}
    ).then(response => response.json()
        /*this must be called playerDb as to not get confused with the player object that is being used in the game*/
    ).then(playerDb => {
        console.log("database player id:" + playerDb.playerid);
        console.log("database skin id:" + playerDb.skinid);
        /*the player is created using the parameters of the playerid and skinid from the database*/
        player = new Player(playerDb.playerid, playerDb.skinid);
        console.log("object player id:" + player.playerid);
        console.log("object skin id:" + player.skinid);
        resolve();
    });
});

class Player{
    /*a constructor creates the player object*/
    constructor(playerid, skinid){
        /*these two attributes will be using the values from the database*/
        this.playerid = playerid;
        this.skinid = skinid;

        /*the player's x and y coordinates are in the middle of the playable area*/
        this.x = pw/2;
        this.y = ph/2;
        /*the artificialY will start off as 0*/
        /*though the player isn't technically at the y coordinate of 0, this will be the starting point of every game they start*/
        this.artificialY = 0;

        /*dx is the horizontal velocity of the player and starts off as 0 because the player is not moving left and right when the game loads*/
        this.dx = 0;
        this.dy = 500;

        /*the player's skin is the playerImage object that as declared earlier*/
        this.image = playerImage;
        /*the player starts off as alive, and with 3 lives*/
        this.alive = true;
        this.lives = 3;
        /*this starts off as false, because the player is not attacking when they spawn in*/
        this.attacking = false;
        /*player's score starts off as 0*/
        this.score = 0;
        /*the player's currency is 0 because it's the session currency*/
        this.currency = 0;

        /*if the cooldown is false, then there is no cooldown and the player can attack*/
        this.cooldown = false;
        /*the cooldown timer is the time left of the cooldown, set to 0 at the start because the player has not yet attacked*/
        this.cooldownTimer = 0;
    }
    draw(context){
        /*if the player is dead, they are not drawn*/
        if(!this.alive) return;
        /*the player is drawn in the middle of the playable area*/
        /*half the width and the height are taken away because the drawImage function draws from the corner*/
        context.drawImage(this.image, this.x - this.image.width/2, this.y - this.image.height/2);
    }
    update(frameLength) {
        /*if the player is dead, they are not updated*/
        if (!this.alive) return;


        /*the x coordinate is increased or decreased depending on whether the velocity is positive or negative*/
        this.x += frameLength * this.dx;
        this.artificialY += frameLength * this.dy;

        /*if the cooldown is true then the cooldownTimer will decrease by one framelength per frame*/
        if(this.cooldown) this.cooldownTimer -= frameLength;



        /*if the x coordinate is smaller than half the player's width, then they the x coordinate is reset to that same value*/
        if (this.x < this.image.width/2) {
            this.x = this.image.width/2;
            /*the velocity is set to 0 because technically the player is stopping*/
            this.dx = 0;
        }
        /*the same happens on the other side of the playable area*/
        if (this.x > pw - this.image.width/2) {
            this.x = pw - this.image.width/2;
            this.dx = 0;
        }

    }
}