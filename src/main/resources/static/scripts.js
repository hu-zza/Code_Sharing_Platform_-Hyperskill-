window.addEventListener(
  "resize",
  () => {
    document.querySelector("body").click();
    document.querySelector("div.snippetAction button.dropdown-toggle").blur();
  });