function buySkin(skinid){
    let formData = new FormData();
    formData.append("skinid", skinid);
    fetch('/unlockedSkins/new/', {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(unlockedSkinsDb => {
        if (unlockedSkinsDb.hasOwnProperty('error')) alert(responseData.error);
        getShopInfo();
    });
}
function selectSkin(skinid){
    let formData = new FormData();
    formData.append("skinid", skinid);
    fetch('/players/changeSkin/', {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(unlockedSkinsDb => {
        if (unlockedSkinsDb.hasOwnProperty('error')) alert(responseData.error);
        getShopInfo();
    });
}
function pageLoad() {
    checkToken(
        /*success and fail are defined using arrow notation when checkToken is called.*/
        () => {},
        () => {window.location.href = "/client/login.html";}
    );
    document.getElementById("mainMenuOption").addEventListener("click", ()=> window.location.href = "/client/index.html");
    for(let i = 1; i < 5; i++) {
        document.getElementById("skinSelect" + i).addEventListener("click", function() {selectSkin(i);});
        document.getElementById("skinBuy" + i).addEventListener("click", function() {buySkin(i);});
    }

    getShopInfo();

}
function getShopInfo(){
    fetch('/players/get/' + Cookies.get("token"), {method: 'get'}
    ).then(response => response.json()
    ).then(playerDb => {
        if (playerDb.hasOwnProperty('error')) alert(responseData.error);
        document.getElementById("currency").innerText = "currency: " + playerDb.currency;
        fetch('/skins/list/', {method: 'get'}
        ).then(response => response.json()
        ).then(skinsDb => {
            if (skinsDb.hasOwnProperty('error')) alert(responseData.error);
            for(let skinDb of skinsDb) {
                for(let i = 1; i < 5; i++){
                    if(skinDb.skinid === i){
                        document.getElementById("skin" + i).innerText = skinDb.skinName;
                        fetch('/unlockedSkins/list/', {method: 'get'}
                        ).then(response => response.json()
                        ).then(unlockedSkinsDb => {
                            for(let unlockedSkinDb of unlockedSkinsDb){
                                if(playerDb.playerid === unlockedSkinDb.playerid){
                                    if(unlockedSkinDb.skinid === skinDb.skinid) {
                                        document.getElementById("skinSelect" + i).style.display = "block";
                                        document.getElementById("skinBuy" + i).style.display = "none";
                                        if(unlockedSkinDb.skinid == playerDb.skinid){
                                            document.getElementById("skinSelect" + i).innerText = "selected";
                                        }else{
                                            document.getElementById("skinSelect" + i).innerText = "select";
                                        }
                                    }
                                }else{
                                    document.getElementById("skinBuy" + i).style.display = "block";
                                    document.getElementById("skinBuy" + i).innerText = "buy: " + skinDb.cost;
                                    document.getElementById("skinBuy" + i).style.display = "block";
                                    document.getElementById("skinSelect" + i).style.display = "none";
                                }
                            }
                        });
                    }
                }
            }
        });
    });
}