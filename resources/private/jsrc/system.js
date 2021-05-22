var auth = "/auth/"
var url = "/system/";
var today = new Date();
var currentMonth = today.getMonth();
var currentYear = today.getFullYear();
var escapeKey = 27;
var monthsList = ["JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"];
var daysList = ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"];
var yearNow = 0;

const swalWithBootstrapButtons = Swal.mixin({
    customClass: {
        confirmButton: 'btn btn-success',
        cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false
});
