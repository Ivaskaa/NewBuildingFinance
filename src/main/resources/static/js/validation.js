
function validationInputTextEmpty(element){
    if(!element.value){
        element.style.border = "2px solid red";
        return true;
    } else {
        element.style.border = "1px solid #ced4da";
        return false;
    }
}

function validationInputTextDouble(element){
    if(!isNaN(element.value)){
        // check if it is integer
        if (Number.isInteger(element.value)) {
            element.style.border = "2px solid red";
            return true;
        }
        else {
            element.style.border = "1px solid #ced4da";
            return false;
        }
    } else {
        element.style.border = "2px solid red";
        return true;
    }
}

function validationInputFileEmpty(element){
    if(!element.value){
        element.style.borderColor = "red";
        return true;
    } else {
        element.style.borderColor = "#ced4da"
        return false;
    }
}

function validationInputCheckUniqueUsername(element) {
    let result;
    let data = {
        username: element.value
    }
    $.ajax({
        type: 'get',
        url: '../checkUniqueUsername',
        data: data,
        dataType: "json",
        async: false,
        success: function (check) {
            console.log('success /checkUniqueUsername');
            if (check === 'success') {
                element.style.border = "1px solid #ced4da";
                result = false;
            } else {
                element.style.border = "2px solid red";
                result = true;
                alert('This username is already taken');
            }
        },
        error: function () {
            console.log('error /checkUniqueUsername');
        }
    })
    return result;
}

function validationInputCheckUniquePhone(phone){
    let result;
    let data = {
        phone: phone.value
    }
    $.ajax({
        type: 'get',
        url: '../checkUniquePhone',
        data: data,
        dataType: "json",
        async: false,
        success: function (user){
            console.log('success /checkUniquePhone');
            if(user === 'success') {
                phone.style.border = "1px solid #ced4da";
                result = false;
            } else {
                console.log('phone.value' + phone.value);
                console.log('User.phone' + User.phone);
                if(User.phone === phone.value){
                    phone.style.border = "1px solid #ced4da";
                    result = false;
                } else {
                    phone.style.border = "2px solid red";
                    result = true;
                }
            }
        },
        error: function() {
            console.log('error /checkUniquePhone');
        }
    });
    return result !== false;
}

function validationInputTextPhoneMask(element){
    if (element.value.includes('_')){
        element.style.border = "2px solid red";
        return true;
    } else {
        element.style.border = "1px solid #ced4da";
        return false;
    }
}
