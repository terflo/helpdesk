async function add() {

    let inputFile = $("#files")[0]
    if(inputFile.files.length > 3) {
        showToast("Выберите до 3 файлов", 'warning')
        return
    }

    let imagesBase64 = []

    for(let i = 0; i < inputFile.files.length; i++) {
        await getBase64(inputFile.files[i]).then(data => imagesBase64.push(data))
    }

    let modal = new bootstrap.Modal(document.getElementById('loaderModal'), {
        keyboard: false,
        backdrop: 'static'
    })

    modal.show()

    $.ajax({
        type: "POST",
        url: "/requests",
        data: JSON.stringify({
            name: $("#name").val(),
            description: $("#description").val(),
            priority: $("#priority").val(),
            imagesBase64: imagesBase64
        }),
        dataType: "json",
        contentType: "application/json",
        cache: false,
        timeout: 600000,
        success: function () {
                window.location.replace("/requests")
        },
        error: function (data) {
            modal.hide()
            data.responseJSON.forEach(element => showToast(element, 'warning'))
        }

    });
}

function showToast(text, theme) {
    new Toast({
        title: false,
        text: text,
        theme: theme,
        autohide: true,
        interval: 2500
    })
}

function getBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });
}