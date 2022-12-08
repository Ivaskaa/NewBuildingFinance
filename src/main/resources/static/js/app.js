
let contextApp = window.location.pathname.substring(0, window.location.pathname.indexOf("/",0));
let urlApp = window.location.protocol+"//"+ window.location.host + contextApp;

let stompClient = null;

$(document).ready(function() {
    connect();
    getNotifications();
});

function sendNotifications() {
    stompClient.send("/app/notifications", {});
}

function roundPlus(x, n) { //x - число, n - количество знаков
    if(isNaN(x) || isNaN(n)) return false;
    let m = Math.pow(10,n);
    return Math.round(x*m)/m;
}

function presEnterToNextInput(fromInputId, toInputId) {
    if($('#' + fromInputId).is("select")){
        document.getElementById(toInputId).focus();
    } else if($('#' + fromInputId).is("input")){
        $('#' + fromInputId).keyup(function(event) {
            if (event.keyCode === 13) {
                document.getElementById(toInputId).focus();
            }
        });
    }
}

function sort(element, field){
    $("th > div > div").each(function(i, item){
        $(item).removeClass("triangle-desc triangle-asc");
    });
    let triangle = $(element).children('div');
    if(sortingDirection === 'ASC'){
        sortingDirection = 'DESC';
        $(triangle[0]).addClass("triangle-desc");
    } else {
        sortingDirection = 'ASC';
        $(triangle[0]).addClass("triangle-asc");
    }
    sortingField = field;
    updatePage();
}

function sendCurrency() {
    let object = {};
    object.id = IdForUpdating;
    object.cashRegister = document.getElementById('cashRegister').value;
    object.name = document.getElementById('name').value;
    object.price = document.getElementById('price').value;
    stompClient.send("/app/currency", {}, JSON.stringify(object));
}

function connect() {
    let socket = new SockJS('/websockets');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/currency', function (data) {
            let currency = JSON.parse(data.body);
            document.getElementById(currency.id).innerHTML = currency.name + ': ' + currency.price;
        });
        stompClient.subscribe('/topic/notifications', function (data) {
            console.log('/notifications  ---  ' + data);
            getNotifications();
        });
    });
}

function getNotifications() {
    $.ajax({
        type: 'get',
        url: urlApp + '/settings/getNotifications',
        dataType: "json",
        success: function (data) {
            console.log('success /getNotifications');
            $("#notification").empty();
            let number = 0;
            data.forEach((element) => {
                if (element.reviewed) {
                    $("#notification").append($("" +
                        "<a style='color: green' class='dropdown-item' href='/settings/notification/" + element.id + "'>" + element.name + "</a>"
                    ));
                } else {
                    $("#notification").append($("" +
                        "<a style='color: orange' class='dropdown-item' href='/settings/notification/" + element.id + "'>" + element.name + "</a>"
                    ));
                    number++;
                }
            })
            document.getElementById('dropdownMenuButton').innerHTML = 'Notifications (' + number + ')';
        },
        error: function () {
            console.log('error /getNotifications');
        }
    });
}

function success(){
    swal("Success", {
        icon: "success",
        timer: 1000,
        buttons: false
    });
}

function error(){
    swal("Something wrong", {
        icon: "error",
        timer: 1000,
        buttons: false
    });
}

function deleteObjFirst(preUrl, url, objectId) {
    swal({
        title: "Are you sure?",
        icon: "warning",
        buttons: {
            cancel : 'Cancel',
            confirm : {text: 'Delete', className: 'sweet-warning'},
        },
        dangerMode: true
    }).then((willDelete) => {
        if (willDelete) {
            $.ajax({
                type: 'post',
                url: preUrl + url,
                dataType: "json",
                data: { id: objectId },
                success: function () {
                    success();
                    console.log('success ' + url);
                    afterDeleteObjFirst();
                },
                error: function() {
                    error();
                    console.log('error ' + url);
                }
            });
        }
    });
}

function deleteObjSecond(preUrl, url, objectId) {
    swal({
        title: "Are you sure?",
        icon: "warning",
        buttons: {
            cancel : 'Cancel',
            confirm : {text: 'Delete', className: 'sweet-warning'},
        },
        dangerMode: true
    }).then((willDelete) => {
        if (willDelete) {
            $.ajax({
                type: 'post',
                url: preUrl + url,
                dataType: "json",
                data: { id: objectId },
                success: function () {
                    success();
                    console.log('success ' + url);
                    afterDeleteObjSecond();
                },
                error: function() {
                    error();
                    console.log('error ' + url);
                }
            });
        }
    });
}
