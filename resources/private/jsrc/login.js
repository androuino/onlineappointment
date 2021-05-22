m2d2.ready($ => {
    $(logincontainer, {
        show : true
    });
    var loginEmail = $(loginemail, {});
    var loginPassword = $(loginpassword, {});
    $(loginbutton, {
        onclick : function(ev) {
            $.post(auth + "login/", {
                user: loginEmail.value,
                pass: loginPassword.value
            }, function() { // successful
                $.alert("Successful!");
                logincontainer.show = false;
                appointmentsview.show = true;
                userlist.getAllAppointments();
            }, function() { // unsuccessful
                $.alert("Unsuccessful!")
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