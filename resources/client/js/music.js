class Music{
    constructor(src) {
        /*the audio attribute is set to an HTML audio element*/
        this.audio = document.createElement("audio");
        /*the source of the file is passed in as a parameter*/
        this.audio.src = src;
        /*the audio is set to 10% volume*/
        this.audio.volume = 0.1;
        /*the audio loops once it finished*/
        this.audio.loop = true;
        /*the entire file should be loaded when the page loads*/
        this.audio.setAttribute("preload", "auto");
        /*no controls such as pause or play should be displayed*/
        this.audio.setAttribute("controls", "none");
        /*the actual element shouldn't be displayed*/
        this.audio.style.display = "none";
    }
    play(){
        /*when played, the element is added to the document*/
        document.body.appendChild(this.audio);
        /*the audio plays*/
        this.audio.play();
    }
}