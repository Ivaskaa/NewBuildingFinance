function sort(element, field){
    $("thead > tr > th > div > div").each(function(i, item){
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

function roundPlus(x, n) { //x - число, n - количество знаков
    if(isNaN(x) || isNaN(n)) return false;
    let m = Math.pow(10,n);
    return Math.round(x*m)/m;
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