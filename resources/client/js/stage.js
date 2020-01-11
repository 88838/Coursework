let stage;
/*like with the monsters, the stageImages is replaced with a more generalised stagesInfo array*/
const stagesInfo = [];

/*this was previously loadStageImage*/
let loadStagesInfo = new Promise(function(resolve) {
    let loadedImageCount = 0;
    let loadCheck = function(stagesDb) {
        loadedImageCount++;
        if (loadedImageCount === stagesDb.length) {
            resolve();
        }
    };
    fetch('/stages/list' , {method: 'get'}
    ).then(response => response.json()
    ).then(stagesDb => {
        for (let stageDb of stagesDb) {
            let image = new Image();
            image.src = stageDb.imageFile;
            image.onload = () => loadCheck(stagesDb);
            /*these are the three attributes */
            stagesInfo.push([stageDb.stageid, image, stageDb.locationY]);
        }
    });
});

class Stage{
    /*the id is passed into the constructor*/
    constructor(stageid, image) {
        this.stageid = stageid;
        /*the image is set to the id-1 because arrays start with the index 0*/
        this.image = image;
        /*the starting y coordinate is in the middle of the canvas*/
        this.y = ph/2;
        /*the stage is scrolling upwards, so it has a negative velocity*/
        this.dy = -200;
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
/*        this.image = stageImages[this.stageid-1];*/
        /*when the y coordinate travels 595 pixels upwards (the whole height of the image -5, to account for frame skipping), then the y coordinate is reset*/
        if(this.y <= (-this.image.height/2+5)) this.y = ph/2;
        this.y += frameLength * this.dy;
    }

    setImage(){
        /*if the player's artificial y coordinate is bigger than or equal to the location of where the stage starts, then the stage changes id and image*/
        for (let stageInfo of stagesInfo){
            if(player.artificialY >= stageInfo[2]){
                    this.stageid = stageInfo[0];
                    this.image = stageInfo[1];
            }
        }
    }
}