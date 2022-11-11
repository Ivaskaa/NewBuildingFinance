
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