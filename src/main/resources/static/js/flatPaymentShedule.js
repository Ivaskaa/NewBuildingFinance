
let modalBuyer = document.getElementById("popupBuyer");

let Buyer = {};
let RealtorId = 0;
let AgencyId = 0;

$(document).ready(function() {
    $('#buyer').select2({
        placeholder: "Choose buyer",
        minimumInputLength: 3,
        ajax: {
            url: url + '/flats/getBuyersByName',
            dataType: "json",
            type: "get",
            delay: 400,
            processResults: function (data) {
                return {
                    results: $.map(data, function (item) {
                        return {
                            text: item.name + " " + item.surname,
                            id: item.id
                        };
                    })
                };
            },
            cache: true
        }
    });
})

function updateAgencyAndRealtor(id){
    let data = {id: id};
    $.ajax({
        type: 'get',
        url: url +'/flats/getBuyerById',
        dataType: "json",
        data: data,
        success: function (data){
            console.log('success /getBuyerById');
            Buyer = data;
            $('#buyer').append($("" +
                "<option value='" + data.id + "' selected>" + data.name + " " + data.surname + "</option>"))
            RealtorId = data?.realtor?.id;
            AgencyId = data?.realtor?.agency?.id;
            $("#agencyDiv").show();
            $("#realtorDiv").show();
            updateAgency();
        },
        error: function() { console.log('error /getBuyerById'); }
    });
}

function updateAgency(){
    $.ajax({
        type: 'get',
        url: url +'/flats/getAllAgencies',
        dataType: "json",
        success: function (data){
            console.log('success /getAllAgencies');
            $("#agencyy").empty();
            $("#agencyy").append($("" +
                "<option selected disabled value='" + null + "'>Choose agency</option>"
            ));
            data.forEach((object) => {
                if (object.id === AgencyId) {
                    $("#agencyy").append($("" +
                        "<option selected value='" + object.id + "'> " + object.name + "</option>"
                    ));
                    updateRealtor();
                } else if(object.id === Flat?.realtor?.agency?.id ?? null) {
                    if (AgencyId === 0) {
                        $("#agencyy").append($("" +
                            "<option selected value='" + object.id + "'> " + object.name + "</option>"
                        ));
                        $("#agencyDiv").show();
                        $("#realtorDiv").show();
                        updateRealtor();
                    }
                } else {
                    $("#agencyy").append($("" +
                        "<option value='" + object.id + "'> " + object.name + "</option>"
                    ));
                }
            })
            AgencyId = 0;
        },
        error: function() { console.log('error /getAllAgencies'); }
    });
}

function updateRealtor(){
    let data = {id:$("#agencyy").val()};
    $.ajax({
        type: 'get',
        url: url +'/flats/getRealtorsByAgenciesId',
        dataType: "json",
        data: data,
        success: function (data){
            console.log('success /getRealtorsByAgenciesId');
            $("#realtorr").empty();
            $("#realtorr").append($("" +
                "<option selected disabled value='" + null + "'>Choose realtor</option>"
            ));
            data.forEach((object) => {
                if (object.id === RealtorId) {
                    $("#realtorr").append($("" +
                        "<option selected value='" + object.id + "'> " + object.name + " " + object.surname + "</option>"
                    ));
                } else if(object.id === Flat?.realtor?.id ?? null) {
                    if (RealtorId === 0) {
                        $("#realtorr").append($("" +
                            "<option selected value='" + object.id + "'> " + object.name + " " + object.surname + "</option>"
                        ));
                    }
                } else {
                    $("#realtorr").append($("" +
                        "<option value='" + object.id + "'> " + object.name + " " + object.surname + "</option>"
                    ));
                }
            })
            RealtorId = 0;
        },
        error: function() { console.log('error /getRealtorsByAgenciesId'); }
    });
}

function openPopupBuyer() {
    modalBuyer.style.display = "block";
    $("#popupBuyer").append($(
        "<div class=\"modal-content\">\n" +
        "   <span onclick=\"closePopupBuyer()\" class=\"close\">&times;</span>\n" +
        "     <form class=\"mt-2\">\n" +
        "       <div class='row'>" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>Name:</label>\n" +
        "           <input id=\"buyerName\" type=\"text\" maxlength=\"50\" \n" +
        "                  placeholder=\"Enter name\" class=\"form-control\">\n" +
        "           <p id='buyerNameMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>\n" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>Surname:</label>\n" +
        "           <input id=\"buyerSurname\" type=\"text\" maxlength=\"50\" \n" +
        "                  placeholder=\"Enter surname\" class=\"form-control\">\n" +
        "           <p id='buyerSurnameMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>\n" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>Lastname:</label>\n" +
        "           <input id=\"buyerLastname\" type=\"text\" maxlength=\"50\" \n" +
        "                  placeholder=\"Enter lastname\" class=\"form-control\">\n" +
        "           <p id='buyerLastnameMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>" +
        "       </div>\n" +
        "       <div class='row'>" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>Address:</label>\n" +
        "           <input id=\"buyerAddress\" type=\"text\" maxlength=\"50\" \n" +
        "                  placeholder=\"Enter address\" class=\"form-control\">\n" +
        "           <p id='buyerAddressMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>\n" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>ID number:</label>\n" +
        "           <input id=\"buyerIdNumber\" type=\"number\" maxlength=\"15\" \n" +
        "                  placeholder=\"Enter ID\" class=\"form-control\">\n" +
        "           <p id='buyerIdNumberMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>" +
        "       </div>\n" +
        "       <div class='row'>" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>Agency:</label>\n" +
        "           <select id=\"buyerAgency\" onchange='getRealtors()' class=\"form-control\">\n" +
        "           </select>" +
        "           <p id='buyerAgencyMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>Realtor:</label>\n" +
        "           <select id=\"buyerRealtor\" class=\"form-control\">\n" +
        "             <option value='" + null + "' selected disabled>Choose realtor</option>" +
        "           </select>" +
        "           <p id='buyerRealtorMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>Manager:</label>\n" +
        "           <select id=\"buyerManager\" class=\"form-control\">\n" +
        "           </select>" +
        "           <p id='buyerManagerMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>" +
        "       </div>" +
        "       <h3>Passport data</h3>" +
        "       <div class='row'>" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>Series:</label>\n" +
        "           <input id=\"buyerPassportSeries\" type=\"number\" min='0' maxlength=\"50\" \n" +
        "                  placeholder=\"Enter passport series\" class=\"form-control\">\n" +
        "           <p id='buyerPassportSeriesMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>Number:</label>\n" +
        "           <input id=\"buyerPassportNumber\" type=\"number\" min='0' maxlength=\"50\" \n" +
        "                  placeholder=\"Enter passport number\" class=\"form-control\">\n" +
        "           <p id='buyerPassportNumberMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>" +
        "       <div class='col'>" +
        "         <div class=\"form-group mt-2\">\n" +
        "           <label>Who issued:</label>\n" +
        "           <input id=\"buyerPassportWhoIssued\" type=\"number\" min='0' maxlength=\"50\" \n" +
        "                  placeholder=\"Enter who issued\" class=\"form-control\">\n" +
        "           <p id='buyerPassportWhoIssuedMessage' style=\"color: red\"></p>" +
        "         </div>\n" +
        "       </div>" +
        "       </div>" +
        "       <h3>Buyer contacts</h3>" +
        "       <div class=\"form-group mt-2\">\n" +
        "         <label>Phone:</label>\n" +
        "         <input id=\"buyerPhone\" type=\"text\" maxlength=\"50\" \n" +
        "                placeholder=\"Enter phone\" class=\"form-control\">\n" +
        "         <p id='buyerPhoneMessage' style=\"color: red\"></p>" +
        "       </div>\n" +
        "       <div class=\"form-group mt-2\">\n" +
        "         <label>Email:</label>\n" +
        "         <input id=\"buyerEmail\" type=\"text\" maxlength=\"50\" \n" +
        "                placeholder=\"Enter email\" class=\"form-control\">\n" +
        "         <p id='buyerEmailMessage' style=\"color: red\"></p>" +
        "       </div>\n" +
        "       <div class=\"form-group mt-2\">\n" +
        "         <label>Note:</label>\n" +
        "         <textarea id=\"buyerNote\" maxlength=\"3000\" \n" +
        "                placeholder=\"Enter note\" class=\"form-control\"></textarea>\n" +
        "       </div>\n" +
        "       <button onclick=\"saveBuyer()\" type=\"button\" class=\"btn btn-success mt-2\">Save</button>\n" +
        "     </form>\n" +
        "   </div>"
    ));
    getAgencies();
    getManagers();
}

function closePopupBuyer(){
    modalBuyer.style.display = "none";
    $("#popupBuyer").empty();
}

function getAgencies(){
    $.ajax({
        type: 'get',
        url: url + '/flats/getAllAgencies',
        dataType: "json",
        async: false,
        success: function (data){
            console.log('success /getAllAgencies');
            $("#buyerAgency").append($("" +
                "<option value='" + null + "' selected disabled>Choose agency</option>"
            ));
            data.forEach((object) =>{
                $("#buyerAgency").append($("" +
                    "<option value='" + object.id + "'>" + object.name + "</option>"
                ));
            })
        },
        error: function() {
            console.log('error /getAllAgencies');
        }
    });
}

function getManagers(){
    $.ajax({
        type: 'get',
        url: url + '/flats/getAllManagers',
        dataType: "json",
        async: false,
        success: function (data){
            console.log('success /getAllManagers');
            $("#buyerManager").append($("" +
                "<option value='" + null + "' disabled selected>Choose manager</option>"
            ));
            data.forEach((object) =>{
                $("#buyerManager").append($("" +
                    "<option value='" + object.id + "'>" + object.name + " " + object.surname + "</option>"
                ));
            })
        },
        error: function() {
            console.log('error /getAllManagers');
        }
    });
}

function getRealtors(){
    let data = {};
    data.id = $("#buyerAgency").val()
    $.ajax({
        type: 'get',
        url: url + '/flats/getRealtorsByAgenciesId',
        dataType: "json",
        data: data,
        async: false,
        success: function (data){
            console.log('success /getRealtorsByAgenciesId');
            $("#buyerRealtor").empty();
            $("#buyerRealtor").append($("" +
                "<option value='" + null + "' selected disabled>Choose realtor</option>"
            ));
            data.forEach((object) =>{
                $("#buyerRealtor").append($("" +
                    "<option value='" + object.id + "'>" + object.name + " " + object.surname + "</option>"
                ));
            })
        },
        error: function() {
            console.log('error /getRealtorsByAgenciesId');
        }
    });
}

function saveBuyer(){
    let object = {};
    object.name = document.getElementById('buyerName').value;
    object.surname = document.getElementById('buyerSurname').value;
    object.lastname = document.getElementById('buyerLastname').value;
    object.address = document.getElementById('buyerAddress').value;
    object.idNumber = document.getElementById('buyerIdNumber').value;
    object.passportSeries = document.getElementById('buyerPassportSeries').value;
    object.passportNumber = document.getElementById('buyerPassportNumber').value;
    object.passportWhoIssued = document.getElementById('buyerPassportWhoIssued').value;
    object.phone = document.getElementById('buyerPhone').value;
    object.email = document.getElementById('buyerEmail').value;
    object.note = document.getElementById('buyerNote').value;
    object.realtor = $('#buyerRealtor').val();
    object.manager = $('#buyerManager').val();

    $.ajax({
        type: 'post',
        url: url + '/flats/addBuyer',
        data: JSON.stringify(object),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            let validation = JSON.parse(data);
            if (typeof validation !== "object") {
                updateAgencyAndRealtor(validation);
                closePopupBuyer();
                console.log('success /addBuyer');
            } else {
                validationMessageBuyer(validation);
            }
        },
        error: function () {
            console.log('error /addBuyer');
        }
    });
}

function validationMessageBuyer(validation){
    if(validation?.name ?? false){
        document.getElementById('buyerNameMessage').innerHTML = validation.name;
        document.getElementById('buyerName').style.border = "1px solid red";
    } else {
        document.getElementById('buyerNameMessage').innerHTML = "";
        document.getElementById('buyerName').style.border = "1px solid green";
    }
    if(validation?.surname ?? false){
        document.getElementById('buyerSurnameMessage').innerHTML = validation.surname;
        document.getElementById('buyerSurname').style.border = "1px solid red";
    } else {
        document.getElementById('buyerSurnameMessage').innerHTML = "";
        document.getElementById('buyerSurname').style.border = "1px solid green";
    }
    if(validation?.lastname ?? false){
        document.getElementById('buyerLastnameMessage').innerHTML = validation.lastname;
        document.getElementById('buyerLastname').style.border = "1px solid red";
    } else {
        document.getElementById('buyerLastnameMessage').innerHTML = "";
        document.getElementById('buyerLastname').style.border = "1px solid green";
    }
    if(validation?.address ?? false){
        document.getElementById('buyerAddressMessage').innerHTML = validation.address;
        document.getElementById('buyerAddress').style.border = "1px solid red";
    } else {
        document.getElementById('buyerAddressMessage').innerHTML = "";
        document.getElementById('buyerAddress').style.border = "1px solid green";
    }
    if(validation?.idNumber ?? false){
        document.getElementById('buyerIdNumberMessage').innerHTML = validation.idNumber;
        document.getElementById('buyerIdNumber').style.border = "1px solid red";
    } else {
        document.getElementById('buyerIdNumberMessage').innerHTML = "";
        document.getElementById('buyerIdNumber').style.border = "1px solid green";
    }
    if(validation?.passportSeries ?? false){
        document.getElementById('buyerPassportSeriesMessage').innerHTML = validation.passportSeries;
        document.getElementById('buyerPassportSeries').style.border = "1px solid red";
    } else {
        document.getElementById('buyerPassportSeriesMessage').innerHTML = "";
        document.getElementById('buyerPassportSeries').style.border = "1px solid green";
    }
    if(validation?.passportNumber ?? false){
        document.getElementById('buyerPassportNumberMessage').innerHTML = validation.passportNumber;
        document.getElementById('buyerPassportNumber').style.border = "1px solid red";
    } else {
        document.getElementById('buyerPassportNumberMessage').innerHTML = "";
        document.getElementById('buyerPassportNumber').style.border = "1px solid green";
    }
    if(validation?.passportWhoIssued ?? false){
        document.getElementById('buyerPassportWhoIssuedMessage').innerHTML = validation.passportWhoIssued;
        document.getElementById('buyerPassportWhoIssued').style.border = "1px solid red";
    } else {
        document.getElementById('buyerPassportWhoIssuedMessage').innerHTML = "";
        document.getElementById('buyerPassportWhoIssued').style.border = "1px solid green";
    }
    if(validation?.phone ?? false){
        document.getElementById('buyerPhoneMessage').innerHTML = validation.phone;
        document.getElementById('buyerPhone').style.border = "1px solid red";
    } else {
        document.getElementById('buyerPhoneMessage').innerHTML = "";
        document.getElementById('buyerPhone').style.border = "1px solid green";
    }
    if(validation?.email ?? false){
        document.getElementById('buyerEmailMessage').innerHTML = validation.email;
        document.getElementById('buyerEmail').style.border = "1px solid red";
    } else {
        document.getElementById('buyerEmailMessage').innerHTML = "";
        document.getElementById('buyerEmail').style.border = "1px solid green";
    }
    if(validation?.realtor ?? false){
        document.getElementById('buyerRealtorMessage').innerHTML = validation.realtor;
        document.getElementById('buyerRealtor').style.border = "1px solid red";
    } else {
        document.getElementById('buyerRealtorMessage').innerHTML = "";
        document.getElementById('buyerRealtor').style.border = "1px solid green";
    }
    if(validation?.manager ?? false){
        document.getElementById('buyerManagerMessage').innerHTML = validation.manager;
        document.getElementById('buyerManager').style.border = "1px solid red";
    } else {
        document.getElementById('buyerManagerMessage').innerHTML = "";
        document.getElementById('buyerManager').style.border = "1px solid green";
    }
}


let modalPayment = document.getElementById("popupPayment");

let currentPagePayment = 1;
let sizePayment = 10;
let sortingFieldPayment = 'number';
let sortingDirectionPayment = 'ASC';  // DESC

let alreadyPay;

let totalPagePayment;
let IdForUpdating;

$(document).ready(function() {
    updatePagePayment();
});

function sort(field){
    if(sortingDirectionPayment === 'ASC'){
        sortingDirectionPayment = 'DESC';
    } else {
        sortingDirectionPayment = 'ASC';
    }
    sortingFieldPayment = field;
    updatePagePayment();
}

function openPopupPayment(style){
    modalPayment.style.display = "block";
    let price = Flat.salePrice - alreadyPay;
    $("#popupPayment").append($(
        "<div class=\"modal-content\">\n" +
        "           <span onclick=\"closePopup()\" class=\"close\">&times;</span>\n" +
        "           <form class=\"mt-2\">\n" +
        "               <div class=\"form-group mt-2\">\n" +
        "                   <label>Number:</label>\n" +
        "                   <input id=\"paymentNumber\" type=\"number\" min='1' max='100'\n" +
        "                           placeholder=\"Enter number\" class=\"form-control\">\n" +
        "                   <p id='paymentNumberMessage' style=\"color: red\"></p>" +
        "               </div>\n" +
        "               <div class=\"form-group mt-2\">\n" +
        "                   <label>Date:</label>\n" +
        "                   <input id=\"paymentDate\" type=\"date\" class=\"form-control\">\n" +
        "                   <p id='paymentDateMessage' style=\"color: red\"></p>" +
        "               </div>\n" +
        "               <div class=\"form-group mt-2\" style='display: none'>\n" +
        "                   <label id='stillLeft'>Still left: " + price + "</label>\n" +
        "               </div>\n" +
        "               <div class=\"form-group mt-2\">\n" +
        "                   <label>Planned:</label>\n" +
        "                   <input id=\"paymentPlanned\" oninput='alreadyLeft()' type=\"number\" min='0' max='1000000' placeholder=\"Enter planned\" class=\"form-control\">\n" +
        "                   <p id='paymentPlannedMessage' style=\"color: red\"></p>" +
        "               </div>\n" +
        "               <button onclick=\"savePayment('"  + style + "')\" type=\"button\" class=\"btn btn-success mt-2\">Save</button>\n" +
        "           </form>\n" +
        "       </div>"
    ));
}

function closePopup(){
    modalPayment.style.display = "none";
    $("#popupPayment").empty();
}

function savePayment(style){
    let object = {};
    object.number = document.getElementById('paymentNumber').value;
    object.date = document.getElementById('paymentDate').value;
    object.planned = document.getElementById('paymentPlanned').value;
    object.flatId = Flat.id;

    if(style === 'add') {
        $.ajax({
            type: 'post',
            url: url + '/flats/addPayment',
            data: JSON.stringify(object),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log('success /addPayment');
                let validation = JSON.parse(data);
                if (validation == null) {
                    updatePagePayment()
                    closePopup();
                } else {
                    validationMessagePayment(validation);
                }
            },
            error: function () {
                console.log('error /addPayment');
            }
        });
    }

    if(style === 'edit') {
        object.id = IdForUpdating;
        $.ajax({
            type: 'post',
            url: url + '/flats/updatePayment',
            data: JSON.stringify(object),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log('success /updatePayment');
                let validation = JSON.parse(data);
                if (validation == null) {
                    updatePagePayment()
                    closePopup();
                } else {
                    validationMessagePayment(validation);
                }
            },
            error: function () {
                console.log('error /updatePayment');
            }
        });
    }
}

function validationMessagePayment(validation){
    if(validation?.number ?? false){
        document.getElementById('paymentNumberMessage').innerHTML = validation.number;
        document.getElementById('paymentNumber').style.border = "1px solid red";
    } else {
        document.getElementById('paymentNumberMessage').innerHTML = "";
        document.getElementById('paymentNumber').style.border = "1px solid green";
    }
    if(validation?.date ?? false){
        document.getElementById('paymentDateMessage').innerHTML = validation.date;
        document.getElementById('paymentDate').style.border = "1px solid red";
    }else {
        document.getElementById('paymentDateMessage').innerHTML = "";
        document.getElementById('paymentDate').style.border = "1px solid green";
    }
    if(validation?.planned ?? false){
        document.getElementById('paymentPlannedMessage').innerHTML = validation.planned;
        document.getElementById('paymentPlanned').style.border = "1px solid red";
    }else {
        document.getElementById('paymentPlannedMessage').innerHTML = "";
        document.getElementById('paymentPlanned').style.border = "1px solid green";
    }
}


function updatePagePayment(){
    let data = {
        page: currentPagePayment,
        size: sizePayment,
        field: sortingFieldPayment,
        direction: sortingDirectionPayment,

        flatId: FlatId
    }
    $.ajax({
        type: 'get',
        url: url + '/flats/getPaymentsByFlatId',
        data: data,
        dataType: "json",
        success: function (data){
            totalPagePayment = data.totalPages;
            if(currentPagePayment > totalPagePayment){
                if(currentPagePayment !== 1) {
                    currentPagePayment--;
                    updatePagePayment();
                }
            }
            updateListPayment(data);
            console.log('success /getPaymentsByFlatId');
        },
        error: function() {
            console.log('error /getPaymentsByFlatId');
        }
    });
}

function updateListPayment(data) {
    $("#listPayment").empty();
    alreadyPay = 0;
    data.content.forEach((object) => {
        let actually = object?.actually ?? 'None';
        let remains = object?.remains ?? 'None';
        let date = moment(object.date).format('YYYY-MM-DD');
        alreadyPay += object.planned;
        $("#listPayment").append($("" +
            "<tr>" +
            "<td>" +
            "<p>" + object.number + "</p>" +
            "</td>" +
            "<td>" +
            "<p class='text'>" + date + "</p>" +
            "</td>" +
            "<td>" +
            "<p>" + object.planned + "</p>" +
            "</td>" +
            "<td>" +
            "<p>" + actually + "</p>" +
            "</td>" +
            "<td>" +
            "<p>" + remains + "</p>" +
            "</td>" +
            "<td>" +
            "<button onclick='pay(" + object.id + ")' class=\"btn btn-success\" type=\"button\">Pay</button>" +
            "</td>" +
            "<td>" +
            "<button name='button' onclick='editFormPayment(" + object.id + ")' class=\"btn btn-warning\" type=\"button\" >Edit</button>" +
            "</td>" +
            "<td>" +
            "<button name='button' onclick='deleteObjectPayment(" + object.id + ")' class=\"btn btn-danger\" type=\"button\">Delete</button>" +
            "</td>" +
            "</tr>"
        ));
    })

    let currentDate = new Date();
    let cur_month = currentDate.getMonth() + 1;
    let cur_year = currentDate.getFullYear();

    let elements = document.getElementsByClassName('text');
    for (let item of elements) {
        let ok = new Date(item.innerHTML);
        let month = ok.getMonth() + 1;
        let year = ok.getFullYear();

        if (cur_year > year){
            item.style.color = 'red';
        } else if(cur_year === year && cur_month > month){
            item.style.color = 'red';
        } else if(cur_year === year && cur_month === month){
            item.style.color = 'blue';
        } else {
            item.style.color = 'green';
        }
    }

    if(undefined !== Flat?.contract?.id ?? null) {
        let buttons = document.getElementsByName('button');
        buttons.forEach((element) => {
            element.disabled = true;
        })
    }

    paginationButton();
}

function alreadyLeft(){
    let price = Flat.salePrice - alreadyPay - document.getElementById('paymentPlanned').value;
    let string = 'Still left: ' + price.toString();
    document.getElementById('stillLeft').innerHTML = string;
}

function editFormPayment(id){
    IdForUpdating = id;
    openPopupPayment('edit');
    let data = { id: id };
    $.ajax({
        type: 'get',
        url: url + '/flats/getPaymentById',
        data: data,
        dataType: "json",
        success: function (object){
            console.log('success /getPaymentById');
            document.getElementById('paymentNumber').value = object.number;
            document.getElementById('paymentDate').value = moment(object.date).format('YYYY-MM-DD');
            document.getElementById('paymentPlanned').value = object.planned;
        },
        error: function() {
            console.log('error /getPaymentById');
        }
    });
}

function deleteObjectPayment(id){
    let data = { id: id };
    $.ajax({
        type: 'post',
        url: url + '/flats/deletePaymentById',
        data: data,
        dataType: "json",
        success: function (success){
            console.log(success + ' /deletePaymentById');
            updatePagePayment()
        },
        error: function() {
            console.log('error /deletePaymentById');
        }
    });
}

function changeSize(){
    sizePayment = $('#sizeSelect').val();
    currentPagePayment = 1;
    updatePagePayment()
}

function paginationButton(){
    $("#choose-page").empty();
    if(totalPagePayment > 1){
        if(currentPagePayment !== 1){
            $("#choose-page").append($("" +
                "<div class='page-item'>" +
                "   <button class='page-link' tabIndex=\"-1\" onclick='goFirstPage()' style='height: 40px'>First</button>" +
                "</div>" +
                "<div class='page-item'>" +
                "   <button class='page-link' tabIndex=\"-1\" onclick='goPreviousPage()' style='height: 40px'>\<\<</a>" +
                "</div>"
            ));
        } else {
            $("#choose-page").append($("" +
                "<div class='page-item disabled'>" +
                "   <button class='page-link' tabIndex=\"-1\" onclick='goFirstPage()' style='height: 40px'>First</button>" +
                "</div>" +
                "<div class='page-item disabled'>" +
                "   <button class='page-link' tabIndex=\"-1\" onclick='goPreviousPage()' style='height: 40px'>\<\<</a>" +
                "</div>"
            ));
        }
        let start = currentPagePayment - 2;
        let finish = currentPagePayment + 2;
        for(let i = start; i <= finish; i++) {
            if(i > 0 && i <= totalPagePayment) {
                if (currentPagePayment === i) {
                    $("#choose-page").append($("" +
                        "<div class='page-item active'>" +
                        "<button onclick='goToPage(" + i + ")' class='page-link' tabIndex=\"-1\" style='height: 40px; width: 40px'>" + i + "</button>" +
                        "</div>"
                    ));
                } else {
                    $("#choose-page").append($("" +
                        "<div class='page-item'>" +
                        "<button onclick='goToPage(" + i + ")' class='page-link' tabIndex=\"-1\" style='height: 40px; width: 40px'>" + i + "</button>" +
                        "</div>"
                    ));
                }
            }
        }
        if(currentPagePayment !== totalPagePayment) {
            $("#choose-page").append($("" +
                "<div class='page-item'>" +
                "   <button class='page-link' tabIndex=\"-1\" onclick='goNextPage()' style='height: 40px'>\>\></button>" +
                "</div>" +
                "<div class='page-item'>" +
                "   <button class='page-link' tabIndex=\"-1\" onclick='goLastPage()' style='height: 40px'>Last</a>" +
                "</div>"
            ));
        } else {
            $("#choose-page").append($("" +
                "<div class='page-item disabled'>" +
                "   <button class='page-link' tabIndex=\"-1\" onclick='goNextPage()' style='height: 40px'>\>\></button>" +
                "</div>" +
                "<div class='page-item disabled'>" +
                "   <button class='page-link' tabIndex=\"-1\" onclick='goLastPage()' style='height: 40px'>Last</a>" +
                "</div>"
            ));
        }
    }
}

function goFirstPage(){
    currentPagePayment = 1;
    updatePagePayment();
}

function goPreviousPage(){
    currentPagePayment--;
    updatePagePayment();
}

function goToPage(i){
    currentPagePayment = i;
    updatePagePayment();
}
function goNextPage(){
    currentPagePayment++;
    updatePagePayment();
}
function goLastPage(){
    currentPagePayment = totalPagePayment;
    updatePagePayment();
}

