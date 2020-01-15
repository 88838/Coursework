let kills = [];
class Kill{
    /*only a constructor is needed because the kills aren't being drawn*/
    constructor(monsterid){
        this.monsterid = monsterid;
        /*the sessionKills starts off at 1, because when the kill is first pushed, it will be the first kill*/
        this.sessionKills = 1;
    }
}
