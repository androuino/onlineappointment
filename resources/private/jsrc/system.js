var url = "/system/"
var now = new Date();
now.setMinutes(now.getMinutes() - now.getTimezoneOffset());

document.addEventListener("DOMContentLoaded", () => {
    var dateNow = now.toISOString().slice(0,16);
    let data = {}
    function updateData() {
        loading.show = true;
        table.items.length = 0; // This will clear all the elements without destroying the object
        xhr.get(url + "appts/0/5", (res) => {
            if (res.ok) {
                loading.show = false
                res.data.forEach( p => {
                    table.items.push({
                        firstName        : p.firstName,
                        lastName         : p.lastName,
                        email            : p.email,
                        contactNumber    : p.contactNumber,
                        appointmentType : p.apptType,
                        appointmentDate : p.apptDate.replace("T", " "),
                        onclick : (ev) => {
                            form.form.pid.value = p.pid;
                            form.form.firstName.value = p.firstName;
                            form.form.lastName.value = p.lastName;
                            form.form.email.value = p.email;
                            form.form.contactNumber.value = p.contactNumber;
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
                value : ""
            },
            lastName : {
                value : ""
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
                    xhr.post(url + "update/" + form.form.pid.value, data, (res) => {
                        if (res.ok) {
                            //This will redirect to user page after setting cookies.
                            //You can also hide/show parts of the page
                            //window.location.href = res.url;
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
        items : []
    });
    //------------ INIT --------------
    updateData();
});
