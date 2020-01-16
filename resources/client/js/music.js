class Music{
    constructor(src) {
        this.sound = document.createElement("audio");
        this.sound.src = src;
        this.sound.setAttribute("preload", "auto");
        this.sound.setAttribute("controls", "none");
        this.sound.volume = 0.1;
        this.sound.loop = true;
        this.sound.style.display = "none";
        document.body.appendChild(this.sound);

    }
    play(){
        let playPromise = this.sound.play();

        if (playPromise !== undefined) {
            playPromise.then(() => {
            }).catch(error => {});
        }
    }
}