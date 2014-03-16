$(function() {
    $("#Owner").autocomplete({
        source: function(request, response) {
            $.ajax({
                url: "/domains/list",
                data: {
                    max: 10,
                    q: request.term
                },
                success: function(data) {
                    response(data);
                }
            });
        },
        minLength: 2
    });
});
