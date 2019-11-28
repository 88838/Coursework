let w = 0, h = 0;

function pageLoad() {
    window.addEventListener("resize", fixSize);
    fixSize();
}
function fixSize() {
    w = window.innerWidth;
    h = window.innerHeight;

    const content = document.getElementById('content');
    content.width = w;
    content.height = h;
}


