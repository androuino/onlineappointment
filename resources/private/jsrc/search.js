m2d2.ready($ => {
    $(searchparent, {
        input : {
            value : "",
            onkeyup : (ev) => {
                if (userlist.items.length > 0) {
                    userlist.items.forEach(li => {
                        var lName   = li.lastName.text.toUpperCase();
                        var fName   = li.firstName.text.toUpperCase();
                        var email   = li.email.text;
                        var cNumber = li.contactNumber.text;
                        var aType   = li.appointmentType.text.toUpperCase();
                        var aDate   = li.appointmentDate.text.toUpperCase();
                        var filter  = ev.target.value.toUpperCase();
                        if (lName.indexOf(filter) > -1 ||
                            fName.indexOf(filter) > -1 ||
                            email.indexOf(ev.target.value) > -1 ||
                            cNumber.indexOf(ev.target.value) > -1 ||
                            aType.indexOf(filter) > -1 ||
                            aDate.indexOf(filter) > -1) {
                            li.show = true;
                        } else {
                            li.show = false;
                        }
                    })
                } else {
                    console.log("List is empty.");
                }
                if (ev.key === "Escape" || ev.which === escapeKey) {
                    ev.target.value = "";
                    userlist.items.forEach(li => {
                        li.show = true;
                    })
                }
            }
        }
    });
});