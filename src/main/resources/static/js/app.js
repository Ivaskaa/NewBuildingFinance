
let stompClient = null;

$(document).ready(function() {
    connect();
});

// function setConnected(connected) {
//     $("#connect").prop("disabled", connected);
//     $("#disconnect").prop("disabled", !connected);
//     if (connected) {
//         $("#conversation").show();
//     }
//     else {
//         $("#conversation").hide();
//     }
//     $("#greetings").html("");
// }

function connect() {
    let socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/greetings', function (data) {
            let currency = JSON.parse(data.body);
            document.getElementById(currency.id).innerHTML = currency.name + ': ' + currency.price;
        });
    });
}

// function disconnect() {
//     if (stompClient !== null) {
//         stompClient.disconnect();
//     }
//     setConnected(false);
//     console.log("Disconnected");
// }

// function sendName() {
//     let ok = {
//         cashRegister: 'hello',
//         name: 'name',
//         price: 21.2
//     }
//     stompClient.send("/app/hello", {}, JSON.stringify(ok));
// }
//
// $(document).ready(function() {
//     connect();
//     setTimeout(function() {
//         sendName();
//     }, 5000);
// })

// $(function () {
//     $("form").on('submit', function (e) {
//         e.preventDefault();
//     });
//
//     // $( "#connect" ).click(function() { connect(); });
//     // $( "#disconnect" ).click(function() { disconnect(); });
//     $( "#send" ).click(function() { sendName(); });
// });