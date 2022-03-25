$(document).ready(function(){
    $('#password-checkbox').click(function(){
        let password = $('#password')
        $(this).is(':checked') ? password.attr('type', 'text') : password.attr('type', 'password');
    });
});