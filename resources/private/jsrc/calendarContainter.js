m2d2.ready($ => {
    var calendar = $(calendarcontainer, {
        daysInMonth : function(month, year) {
            return 32 - new Date(year, month, 32).getDate();
        },
        showCalendar : function(month, year) {
            if (tableHeader.items.length === 0) {
                tableHeader.items.clear();
                daysList.forEach((day) => {
                    tableHeader.items.push({
                        text : day
                    });
                })
                tableHeader.items.forEach((day) => {
                    if (day.text === "SUN") {
                        day.style = "background-color: red; color: white;"
                    } else if (day.text === "SAT") {
                        day.style = "background-color: blue; color: white;"
                    }
                })
                months.items.clear();
                monthsList.forEach((month) => {
                    months.items.push({
                        option : { text : month }
                    });
                })
                years.items.clear();
                for (let year = yearNow; year < 2101; year++) {
                    years.items.push({
                        option : { text : year }
                    });
                }
            }
            // clear the table body
            tableBody.items.clear();
            let firstDay = (new Date(year, month)).getDay();
            // filling data about month and in the page vio DOM.
            monthAndYear.text = monthsList[month] + " " + year;
            getSelectedYear.selectedIndex = year;
            getSelectedMonth.selectedIndex = month;
            // creating cells
            let date = 1;
            for (let i = 0; i < 6; i++) {
                let tr = $.newNode("tr");
                // create table rows
                for (let j = 0; j < 7; j++) {
                    if (i === 0 && j < firstDay) {
                        let td = $.newNode("td");
                        td.textContent = "";
                        tr.appendChild(td);
                    } else if (date > this.daysInMonth(month, year)) {
                        break;
                    } else {
                        let td = $.newNode("td");
                        if (date === today.getDate() && year === today.getFullYear() && month === today.getMonth()) {
                            td.classList.add("bg-info");
                        }
                        td.textContent = date;
                        td.onclick = function() {
                            Swal.fire(td.textContent);
                            //swalWithBootstrapButtons.fire(td.textContent);
                        }
                        tr.appendChild(td);
                        date++;
                    }
                }
                tableBody.items.push(tr);
            }
        },
        onJump : function() {
            currentMonth = parseInt(getSelectedMonth.selectedIndex);
            currentYear = parseInt(getSelectedYear.selectedIndex);
            calendar.showCalendar(currentMonth, currentYear);
        }
    });
    var tableHeader = $(tableheader, {
        template : {
            th : {
                text : ""
            }
        }
    });
    var tableBody = $(calendarbody, {
        template : {}
    });
    var months = $(monthList, {
        template : {
            option : {
                text : "",
                value : ""
            }
        }
    });
    var years = $(yearList, {
        template : {
            option : {
                text : ""
            }
        }
    });
    var getSelectedYear = $(yearList, {
        selectedIndex : 0,
        onchange : function() {
            calendar.onJump();
        }
    });
    var getSelectedMonth = $(monthList, {
        selectedIndex : 0,
        onchange : function() {
            calendar.onJump();
        }
    });
    var monthAndYear = $(monthandyear, {
        text : ""
    });
    $(iform, {
        pid : {
            value : ""
        },
        lastName : {
            value : "",
            onkeyup : function(ev) {
                var arr = ev.target.value.split(' ');
                var result = '';
                for (var x = 0; x < arr.length; x++) {
                    result += arr[x].substring(0, 1).toUpperCase() + arr[x].substring(1) + ' ';
                }
                ev.target.value = result.substring(0, result.length - 1);
            }
        },
        firstName : {
            value : "",
            onkeyup : function(ev) {
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
        onsubmit : function(ev) {
            const data = getFormData(ev);
            if (typeof data.firstName != 'undefined' && typeof data.lastName != 'undefined' &&
                typeof data.email != 'undefined' && typeof data.contactNumber != 'undefined' &&
                typeof data.apptType != 'undefined' && typeof data.apptDate != 'undefined') {
                var id = (pid.value != 'undefined') ? 0 : pid.value;
                $.post(url + "update/" + id, data, (res) => {
                    if (res.ok) {
                        userList.getAllAppointments();
                        swalWithBootstrapButtons.fire('Record Saved!');
                    } else {
                        swalWithBootstrapButtons.fire(
                            'Oops...',
                            'Something went wrong!',
                            'error'
                        );
                    }
                }, () => {
                    swalWithBootstrapButtons.fire(
                        'Oops...',
                        'Server error',
                        'error'
                    );
                });
            } else {
                swalWithBootstrapButtons.fire(
                    'Oops...',
                    'Data is empty!',
                    'error'
                );
            }
        },
        deleteButton : {
            onclick : function(ev) {
                deleteRecord(pid.value);
            }
        },
        clearButton : {
            onclick : function(ev) {
                clear();
            }
        },
        previous : {
            onclick : function(ev) {
                currentYear = (currentMonth === 0) ? currentYear - 1 : currentYear;
                currentMonth = (currentMonth === 0) ? 11 : currentMonth - 1;
                calendar.showCalendar(currentMonth, currentYear);
            }
        },
        next : {
            onclick : function(ev) {
                currentYear = (currentMonth === 11) ? currentYear + 1 : currentYear;
                currentMonth = (currentMonth + 1) % 12;
                calendar.showCalendar(currentMonth, currentYear);
            }
        },
        getFormData : function(ev) {
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
        },
        getRecord : function(ev) {
            loading.show = true;
            $.get(url + "get_record/" + id, (res) => {
                if (res.ok) {
                    loading.show = false;
                    let data = JSON.parse(res.data);
                    this.pid.value              = data.pid;
                    this.lastName.value         = data.lastName;
                    this.firstName.value        = data.firstName;
                    this.email.value            = data.email;
                    this.contactNumber.value    = data.contactNumber;
                    this.apptType.value         = data.apptType;
                    this.deleteButton.disabled  = false;
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
        deleteRecord : function(id) {
            swalWithBootstrapButtons.fire({
                title : 'Are you sure?',
                text : "This will permanently delete this record.",
                icon : "warning",
                showCancelButton : true,
                confirmButtonText : 'Yes, delete it!',
                cancelButtonText : 'No, cancel!',
                reverseButtons : true
            }).then((result) => {
                if (result.isConfirmed) {
                    $.delete(url + "delete/" + id, (res) => {
                        if (res.ok) {
                            userList.getAllAppointments();
                            clear();
                            swalWithBootstrapButtons.fire(
                                'Deleted!',
                                'Successfully deleted the record.',
                                'success'
                            );
                        } else {
                            swalWithBootstrapButtons.fire(
                                'Oops',
                                'Failed deleting the record',
                                'error'
                            );
                        }
                    }, () => {
                        swalWithBootstrapButtons.fire(
                            'Oops',
                            'Server error',
                            'error'
                        );
                    });
                } else if (result.dismiss === Swal.DismissReason.cancel) {
                    swalWithBootstrapButtons.fire(
                        'Aborted',
                        'error'
                    );
                }
            });
        },
        onload : function() {
            calendar.showCalendar(currentMonth, currentYear);
        },
        clear : function() {
            this.pid.value              = "";
            this.lastName.value         = "";
            this.firstName.value        = "";
            this.email.value            = "";
            this.contactNumber.value    = "";
            this.apptType.value         = "";
            this.deleteButton.disabled  = true;
            search.input.value          = "";
            calendar.show               = false;
        }
    });
});