var headerHeight = document.querySelector('#header').clientHeight;
var backgroundHeight = document.querySelector(".background").clientHeight;

var main = document.querySelector(".head");
main.style.height = backgroundHeight - headerHeight + "px";