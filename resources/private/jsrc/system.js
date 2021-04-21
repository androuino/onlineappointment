var url = "/system/"
var now = new Date();
now.setMinutes(now.getMinutes() - now.getTimezoneOffset());

document.addEventListener("DOMContentLoaded", () => {
    var dateNow = now.toISOString().slice(0,16);
    let data = {}
    function getAllAppointments() {
        loading.show = true;
        table.items.length = 0; // This will clear all the elements without destroying the object
        xhr.get(url + "appts", (res) => {
            if (res.ok) {
                loading.show = false
                res.data.forEach( p => {
                    var number = "" + p.contactNumber;
                    var cNumber = number.replace(number.substring(3, 7), "****");
                    table.items.push({
                        firstName       : p.firstName,
                        lastName        : p.lastName,
                        email           : p.email,
                        contactNumber   : cNumber,
                        appointmentType : p.apptType,
                        appointmentDate : p.apptDate.replace("T", " "),
                        onclick : (ev) => {
                            form.form.pid.value = p.pid;
                            form.form.firstName.value = p.firstName;
                            form.form.lastName.value = p.lastName;
                            form.form.email.value = p.email;
                            form.form.contactNumber.value = number;
                            form.form.apptType.value = p.apptType;
                            form.form.apptDate.value = p.apptDate;
                        }
                    });
                });
            }
        });
    }
    function updateData() {
        loading.show = true;
        table.items.length = 0; // This will clear all the elements without destroying the object
        xhr.get(url + "appts/0/0", (res) => {
            if (res.ok) {
                loading.show = false
                res.data.forEach( p => {
                    var number = "" + p.contactNumber;
                    var cNumber = number.replace(number.substring(3, 7), "****");
                    table.items.push({
                        firstName       : p.firstName,
                        lastName        : p.lastName,
                        email           : p.email,
                        contactNumber   : cNumber,
                        appointmentType : p.apptType,
                        appointmentDate : p.apptDate.replace("T", " "),
                        onclick : (ev) => {
                            form.form.pid.value = p.pid;
                            form.form.firstName.value = p.firstName;
                            form.form.lastName.value = p.lastName;
                            form.form.email.value = p.email;
                            form.form.contactNumber.value = number;
                            form.form.apptType.value = p.apptType;
                            form.form.apptDate.value = p.apptDate;
                        }
                    });
                });
            }
        });
    }
    function getFormData(ev) {
        const form = ev.target;
        const data = {};
        if (form.checkValidity()) {
            const fd = new FormData(form);
            for (let pair of fd.entries()) {
                if (pair[1] != "") {
                    data[pair[0]] = pair[1];
                }
            }
        }
        return data;
    }
    //------------ M2D2 --------------
    var loading = m2d2("#loading", {
        show: false
    });
    var form = m2d2("aside", {
        form : {
            pid : {
                value : ""
            },
            firstName : {
                value : "",
                onkeyup : (ev) => {
                    var arr = ev.target.value.split(' ');
                    var result = '';
                    for (var x = 0; x < arr.length; x++) {
                        result += arr[x].substring(0, 1).toUpperCase() + arr[x].substring(1) + ' ';
                    }
                    ev.target.value = result.substring(0, result.length - 1);
                }
            },
            lastName : {
                value : "",
                onkeyup : (ev) => {
                    var arr = ev.target.value.split(' ');
                    var result = '';
                    for (var x = 0; x < arr.length; x++) {
                        result += arr[x].substring(0, 1).toUpperCase() + arr[x].substring(1) + ' ';
                    }
                    ev.target.value = result.substring(0, result.length - 1);
                }
            },
            email : {
                value : ""
            },
            contactNumber : {
                value : ""
            },
            apptType : {
                value : ""
            },
            apptDate : {
                value : ""
            },
            onsubmit : (ev) => {
                const data = getFormData(ev);
                if (data.firstName != "" && data.lastName != "" &&
                    data.email != "" && data.contactNumber != "" &&
                    data.apptType != "" && data.apptDate != "") { // If validation fails will return empty object
                    console.log(form.form.pid.value);
                    var id = (form.form.pid.value != "undefined") ? 0 : form.form.pid.value
                    xhr.post(url + "update/" + id, data, (res) => {
                        if (res.ok) {
                            //This will redirect to user page after setting cookies.
                            //You can also hide/show parts of the page
                            //window.location.href = res.url;
                            updateData()
                            Swal.fire('Record Saved!')
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Oops...',
                                text: 'Something went wrong!',
                                footer: '<a href>Why do I have this issue?</a>'
                            })
                        }
                    }, () => {
                        console.log("Server error");
                        Swal.fire({
                            icon: 'error',
                            title: 'Oops...',
                            text: 'Server error',
                            footer: '<a href>Why do I have this issue?</a>'
                        })
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: 'Data is empty!',
                        footer: '<a href>Why do I have this issue?</a>'
                    })
                }
            }
        }
    });
    const submit = m2d2("form button", {
        onclick : (ev) => {
            const item = ev.target;
            item.classList.toggle("active");
            if (item.textContent == "Submit") {
                item.textContent = "Submitted!";
                fetch('/system.button')
                    .then(response => response.json())
                    .then(data =>
                        console.log(data.ok)
                    );
           } else {
                item.textContent = "Submit";
            }
        }
    });
    const table = m2d2("#table", {
        items : [
            {
                style : ""
            }
        ],
    });
    const search = m2d2("#searchParent", {
        input : {
            value : "",
            onkeyup : (ev) => {
                if (table.items.length > 0) {
                    table.items.forEach( li => {
                        var fName   = li.firstName.toUpperCase();
                        var lName   = li.lastName.toUpperCase();
                        var email   = li.email;
                        var cNumber = li.contactNumber;
                        var aType   = li.appointmentType.toUpperCase();
                        var aDate   = li.appointmentDate.toUpperCase();
                        var filter  = ev.target.value.toUpperCase();
                        if (fName.indexOf(filter) > -1 ||
                            lName.indexOf(filter) > -1 ||
                            email.indexOf(ev.target.value) > -1 ||
                            cNumber.indexOf(ev.target.value) > -1 ||
                            aType.indexOf(filter) > -1 ||
                            aDate.indexOf(filter) > -1) {
                            li.style = "display: '';";
                        } else {
                            li.style = "display: none;";
                        }
                    });
                } else {
                    console.log("list is empty.");
                }
            }
        }
    });
    //------------ INIT --------------
    getAllAppointments();
});
