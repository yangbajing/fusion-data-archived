$(function() {
    var hiddenText = "[+] Show project info",
        shownText = "[-] Hide project info",
        toggle = $('<a href="#" class="project-info-toggle">' + hiddenText + '</a>'),
        hidden = true,
        infotable = $('table.project-info')

    toggle.insertBefore(infotable)
    toggle.on("click", function(event) {
        if (hidden) {
            infotable.css("display", "block")
            toggle.text(shownText)
            hidden = false
        } else {
            infotable.css("display", "none")
            toggle.text(hiddenText)
            hidden = true
        }
    })
})