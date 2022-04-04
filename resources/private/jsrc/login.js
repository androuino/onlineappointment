m2d2.ready($ => {
    $(logincontainer, {
        show : true
    });
    var loginEmail = $(loginemail, { value : "" });
    var loginPassword = $(loginpassword, { value : "" });
    $(loginbutton, {
        onclick : function(ev) {
            $.post(auth + "login", {
                user: loginEmail.value,
                pass: loginPassword.value
            }, (res) => { // successful
                $.alert("Successful!");
                $.alert(res);
                logincontainer.show = false;
                appointmentsview.show = true;
                userlist.getAllAppointments();
            });
        }
    });
    $(registerbutton, {
        onclick : function(ev) {
            $.alert("register button");
        }
    });
    $(forgotpass, {
        onclick : function(ev) {
            alert("forgot password");
        }
    });
});