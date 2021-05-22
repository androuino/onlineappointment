m2d2.ready($ => {
    $(userlist, {
        template : {
            dataset : { pid : "0" },
            show : true,
            li : {
                lastName : {
                    tagName : "span",
                    className : "lastName",
                    text : "Last Name"
                },
                firstName : {
                    tagName : "span",
                    className : "firstName",
                    text : "First Name"
                },
                email : {
                    tagName : "span",
                    className : "email",
                    text : "E-mail"
                },
                contactNumber : {
                    tagName : "span",
                    className : "contactNumber",
                    text : "Contact Number"
                },
                appointmentType : {
                    tagName : "span",
                    className : "appointmentType",
                    text : "Type"
                },
                appointmentDate : {
                    tagName : "span",
                    className : "appointmentDate",
                    text : "Date & Time"
                },
                onclick : function(ev) {
                    getRecord(ev.currentTarget.dataset.pid);
                    if (!calendar.show) {
                        calendar.show = true;
                    }
                }
            }
        },
        getAllAppointments : function() {
            loading.show = true;
            this.items.clear();
            $.get(url + "appts", (res) => {
                if (res.ok) {
                    loading.show = false;
                    res.data.forEach( item => {
                        var number = "" + item.contactNumber;
                        var cNumber = number.replace(number.substring(3, 7), "****")
                        this.items.push({
                            dataset         : { pid : item.pid },
                            lastName        : { text : item.lastName},
                            firstName       : { text : item.firstName},
                            email           : { text : item.email},
                            contactNumber   : { text : cNumber},
                            appointmentType : { text : item.apptType},
                            appointmentDate : { text : item.apptDate.replace("T", " ")}
                        });
                    });
                }
                loading.show = false;
            });
        },
        getRecord : function(id) {
            loading.show = true;
            $.get(url + "get_record/" + id, (res) => {
                if (res.ok) {
                    loading.show = false;
                    let data = JSON.parse(res.data);
                    iform.pid.value              = data.pid;
                    iform.lastName.value         = data.lastName;
                    iform.firstName.value        = data.firstName;
                    iform.email.value            = data.email;
                    iform.contactNumber.value    = data.contactNumber;
                    iform.apptType.value         = data.apptType;
                    iform.deletebutton.disabled  = false;
                } else {
                    swalWithBootstrapButtons.fire(
                        'Oops...',
                        'Server error',
                        'Error'
                    );
                }
            }, () => {
                swalWithBootstrapButtons.fire(
                    'Oops...',
                    'Server error',
                    'error'
                );
            });
        },
        updateData : function() {
            loading.show = true;
            userlist.items.clear();
            $.get(url + "appts/0/0", (res) => {
                if (res.ok) {
                    loading.show = false;
                    res.data.forEach( item => {
                        var number = "" + item.contactNumber;
                        var cNumber = number.replace(number.substring(3, 7), "****");
                        userlist.items.push({
                            dataset         : { pid : item.pid },
                            lastName        : { text : item.lastName },
                            firstName       : { text : item.firstName },
                            email           : { text : item.email },
                            contactnumber   : { text : cNumber },
                            appointmentType : { text : item.apptType },
                            appointmentDate : { text : item.apptdate.replace("T", " ") }
                        });
                    })
                }
            });
        },
        onload : function() {
            //------------ INIT --------------
            this.getAllAppointments();
        }
    });
});