let w = 0, h = 0;

function pageLoad() {
    window.addEventListener("resize", fixSize);
    fixSize();
}
function fixSize() {
    console.log("Resizing!");
}


