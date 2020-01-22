function getShopInfo(){
    /*the currency must be fetched*/
    fetch('/players/get/' + Cookies.get("token"), {method: 'get'}
    ).then(response => response.json()
    ).then(playerDb => {
        if (playerDb.hasOwnProperty('error')) alert(playerDb.error);
        let currency = playerDb.currency;
        /*if the currency is null, it is set to 0 so that the player understands that they don't have any currency*/
        if(currency === null) currency = 0;
        /*the currency div is populated using their currency*/
        document.getElementById("currency").innerText = "currency: " + currency;
        fetch('/skins/list/', {method: 'get'}
        ).then(response => response.json()
        ).then(skinsDb => {
            if (skinsDb.hasOwnProperty('error')) alert(skinsDb.error);
            /*this loops through all of the skins*/
            for(let skinDb of skinsDb) {
                /*the skin div is populated with the skin's name*/
                document.getElementById("skin" + skinDb.skinid).innerText = skinDb.skinName;
                fetch('/unlockedSkins/get/'  + Cookies.get("token"), {method: 'get'}
                ).then(response => response.json()
                ).then(unlockedSkinsDb => {
                    if (unlockedSkinsDb.hasOwnProperty('error')) alert(unlockedSkinsDb.error);
                    for(let unlockedSkinDb of unlockedSkinsDb){
                        /*if the current unlockedSkin is the same as the current skin that's being looped through the select option is shown and the buy option is hidden*/
                        if (unlockedSkinDb.skinid == skinDb.skinid) {
                            document.getElementById("skinSelect" + skinDb.skinid).style.display = "block";
                            document.getElementById("skinBuy" + skinDb.skinid).style.display = "none";
                            /*if the unlockedSKin being looped through is the same as the skin that the player has selected, then the text says selected*/
                            if (unlockedSkinDb.skinid == playerDb.skinid) {
                                document.getElementById("skinSelect" + skinDb.skinid).innerText = "selected";
                            }else {
                                /*if the player has not selected the current skin in the loop, then the option to select the skin shows up*/
                                document.getElementById("skinSelect" + skinDb.skinid).innerText = "select";
                            }
                            /*if the skin is unlocked, then the loop can break and that skin doesn't need to be checked if it's unlocked anymore*/
                            break;
                        } else {
                            /*if the player has not bought the skin , then the option to buy a skin shows up, with the cost of the skin*/
                            document.getElementById("skinBuy" + skinDb.skinid).innerText = "buy: " + skinDb.cost;
                            document.getElementById("skinBuy" + skinDb.skinid).style.display = "block";
                            document.getElementById("skinSelect" + skinDb.skinid).style.display = "none";

                        }
                    }
                });
            }
        });
    });
}
function buySkin(skinid){
    let formData = new FormData();
    formData.append("skinid", skinid);
    fetch('/unlockedSkins/new/', {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(unlockedSkinsDb => {
        if (unlockedSkinsDb.hasOwnProperty('error')) alert(unlockedSkinsDb.error);
        /*the shop info is refreshed to show which skin the player has bought, what skin is selected, and the currency is updated*/
        getShopInfo();
    });
}
function selectSkin(skinid){
    let formData = new FormData();
    formData.append("skinid", skinid);
    fetch('/players/changeSkin/', {method: 'post', body: formData}
    ).then(response => response.json()
    ).then(unlockedSkinsDb => {
        if (unlockedSkinsDb.hasOwnProperty('error')) alert(unlockedSkinsDb.error);
        getShopInfo();
    });
}
function pageLoad() {

    checkToken(
        () => {},
        () => {window.location.href = "/client/login.html";}
    );
    document.getElementById("mainMenuOption").addEventListener("click", ()=> window.location.href = "/client/index.html");
    /*for each skin in the shop, there is an event listener to see if the player is selecting a skin or buying a skin*/
    for(let i = 1; i < 5; i++) {
        document.getElementById("skinSelect" + i).addEventListener("click", function() {selectSkin(i);});
        document.getElementById("skinBuy" + i).addEventListener("click", function() {buySkin(i);});
    }
    getShopInfo();
}
