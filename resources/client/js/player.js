/*the player variable is created, and will get assigned a value later*/
let player;

/*player image is now skin images because there is more than one image*/
const skinImages = [];

let loadSkinImages = new Promise(function(resolve) {
    let loadedImageCount = 0;
    /*the loadCheck takes in the parameter of the skins that are returned in the form of the json response*/
    let loadCheck = function(skinsDb) {
        loadedImageCount++;
        /*rather than hard coding the amount of images that are meant to load, the length of the json response is used instead*/
        if (loadedImageCount === skinsDb.length) {
            resolve();
        }
    };
    fetch('/skins/list' , {method: 'get'}
    ).then(response => response.json()
    ).then(skinsDb => {
        if (skinsDb.hasOwnProperty('error')) alert(skinsDb.error);
        for (let skinDb of skinsDb) {
/*            console.log(skinDb.imageFile);
            console.log(skinDb.skinid);*/
            let image = new Image();
            /*the temporary image object's source is taken from the skins in the json response*/
            image.src = skinDb.imageFile;
            /*when the image loads, the loadCheck function is run*/
            image.onload = () => loadCheck(skinsDb);
            /*the skin images array is filled with the skinid, and the image object*/
            skinImages.push([skinDb.skinid, image]);
        }
    });
});

/*this promise works in the same way as the ones which are used to load the images of the monster, stage and player*/
let loadPlayer = new Promise(function(resolve) {
    fetch('/players/get/' + Cookies.get("token"), {method: 'get'}
    ).then(response => response.json()
        /*this must be called playerDb as to not get confused with the player object that is being used in the game*/
    ).then(playerDb => {
        if (playerDb.hasOwnProperty('error')) alert(responseData.error);
        /*the player is created using the parameters of the playerid and skinid from the database*/
        player = new Player(playerDb.playerid, playerDb.skinid, playerDb.highScore);
        /*the player's skin must be loaded before the promise can be resolved*/
        resolve();
    });
});



class Player{
    /*a constructor creates the player object*/
    constructor(playerid, skinid, highScore){
        /*these two attributes will be using the values from the database*/
        this.playerid = playerid;
        this.skinid = skinid;
        this.highScore = highScore;

        /*the player's x and y coordinates are in the middle of the playable area*/
        this.x = pw/2;
        this.y = ph/4;
        /*the artificialY will start off as 0*/
        /*though the player isn't technically at the y coordinate of 0, this will be the starting point of every game they start*/
        this.artificialY = 0;

        /*dx is the horizontal velocity of the player and starts off as 0 because the player is not moving left and right when the game loads*/
        this.dx = 0;
        this.dy = 350;

        /*the player's skin is the playerImage object that as declared earlier*/
        this.image = new Image();
        /*the player starts off as alive, and with 3 lives*/
        this.alive = false;
        this.lives = 3;
        /*this starts off as false, because the player is not attacking when they spawn in*/
        this.attacking = false;
        /*player's score starts off as 0*/
        this.score = 0;
        /*the player's currency is 0 because it's the session currency*/
        this.currency = 0;
        this.cumCurrency = 0;

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
    setSkin(){
        /*this loops through every skinImage in the skinImages array*/
        for(let skinImage of skinImages){
            /*if the player's skinid is the same as the one in the array, then the player's image is set to the one in the skins array*/
            if(this.skinid == skinImage[0]) this.image = skinImage[1];
        }
    }
}