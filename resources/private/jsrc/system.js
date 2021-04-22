var url = "/system/"
var now = new Date();
now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
var escapeKey = 27;

document.addEventListener("DOMContentLoaded", () => {
    var dateNow = now.toISOString().slice(0,16);
    let data = {}
    function getAllAppointments() {
        loading.show = true;
        table.items.length = 0; // This will clear all the elements without destroying the object
        xhr.get(url + "appts", (res) => {
            if (res.ok) {
                loading.show = false
                res.data.forEach( item => {
                    var number = "" + item.contactNumber;
                    var cNumber = number.replace(number.substring(3, 7), "****");
                    table.items.push({
                        dataset         : { pid : item.pid },
                        firstName       : { text : item.firstName},
                        lastName        : { text : item.lastName},
                        email           : { text : item.email},
                        contactNumber   : { text : cNumber},
                        appointmentType : { text : item.apptType},
                        appointmentDate : { text : item.apptDate.replace("T", " ")}
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
                res.data.forEach( item => {
                    var number = "" + item.contactNumber;
                    var cNumber = number.replace(number.substring(3, 7), "****");
                    table.items.push({
                        dataset         : { pid : item.pid },
                        firstName       : { text : item.firstName},
                        lastName        : { text : item.lastName},
                        email           : { text : item.email},
                        contactNumber   : { text : cNumber},
                        appointmentType : { text : item.apptType},
                        appointmentDate : { text : item.apptDate.replace("T", " ")},
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
    function getRecord(id) {
        loading.show = true;
        xhr.get(url + "/get_record/" + id, (res) => {
            if (res.ok) {
                loading.show = false
                let data = JSON.parse(res.data);
                form.form.pid.value           = data.pid
                form.form.firstName.value     = data.firstName
                form.form.lastName.value      = data.lastName
                form.form.email.value         = data.email
                form.form.contactNumber.value = data.contactNumber
                form.form.apptType.value      = data.apptType
                form.form.apptDate.value      = data.apptDate
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: 'Server error',
                    footer: 'Something might have happened in server'
                })
            }
        }, () => {
            console.log("Server error");
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'Server error',
                footer: 'Something might have happened in server'
            })
        });
    }
    function deleteRecord(id) {
        Swal.fire({
            title: 'Do you want to delete this record?',
            showCancelButton: true,
            confirmButtonText: `Delete`,
        }).then((result) => {
            /* Read more about isConfirmed, isDenied below */
            if (result.isConfirmed) {
                xhr.delete(url + "/delete/" + id, (res) => {
                    if (res.ok) {
                        getAllAppointments()
                        clear();
                        Swal.fire('Deleted!', '', 'success')
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: 'Oops...',
                            text: 'Server error',
                            footer: 'Something might have happened in server'
                        })
                    }
                }, () => {
                    console.log("Server error");
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: 'Server error',
                        footer: 'Something might have happened in server'
                    })
                });
            } else if (result.isDenied) {
                Swal.fire('Error on deleting a record')
            }
        });
    }
    function clear() {
        form.form.pid.value           = "0";
        form.form.firstName.value     = "";
        form.form.lastName.value      = "";
        form.form.email.value         = "";
        form.form.contactNumber.value = "";
        form.form.apptType.value      = "";
        form.form.apptDate.value      = "";
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
                console.log(data);
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
                            getAllAppointments()
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
            },
            submitDelete : {
                onclick : (ev) => {
                    deleteRecord(form.form.pid.value);
                }
            }
        }
    });
    const submit = m2d2("form button", {
        onclick : (ev) => {
            const item = ev.target;
            item.classList.toggle("active");
            fetch('/system.button')
                .then(response => response.json())
                .then(data =>
                    console.log(data.ok)
                );
        }
    });
    const table = m2d2("#table", {
        template : {
            li : {
                dataset : { pid : "0" },
                show : true,
                firstName : {
                    tagName : "span",
                    className : "firstName",
                    text : "First Name"
                },
                lastName : {
                    tagName : "span",
                    className : "lastName",
                    text : "Last Name"
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
                onclick : (ev) => {
                    getRecord(ev.currentTarget.dataset.pid);
                }
            }
        },
        items : []
    });
    const search = m2d2("#searchParent", {
        input : {
            value : "",
            onkeyup : (ev) => {
                if (table.items.length > 0) {
                    table.items.forEach( li => {
                        var fName   = li.firstName.text.toUpperCase();
                        var lName   = li.lastName.text.toUpperCase();
                        var email   = li.email.text;
                        var cNumber = li.contactNumber.text;
                        var aType   = li.appointmentType.text.toUpperCase();
                        var aDate   = li.appointmentDate.text.toUpperCase();
                        var filter  = ev.target.value.toUpperCase();
                        if (fName.indexOf(filter) > -1 ||
                            lName.indexOf(filter) > -1 ||
                            email.indexOf(ev.target.value) > -1 ||
                            cNumber.indexOf(ev.target.value) > -1 ||
                            aType.indexOf(filter) > -1 ||
                            aDate.indexOf(filter) > -1) {
                            li.show = true;
                        } else {
                            li.show = false;
                        }
                    });
                } else {
                    console.log("list is empty.");
                }
                if (ev.key == "Escape" || ev.which == escapeKey) {
                    ev.target.value = ""
                    table.items.forEach( li => {
                        li.show = true;
                    });
                }
            }
        }
    });
    //------------ INIT --------------
    getAllAppointments();
});
