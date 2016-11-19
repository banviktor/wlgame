angular.module('WLGame').controller('LoginController', function ($http, $httpParamSerializer, user) {
    var controller = this;

    controller.formData = {};
    controller.formError = null;

    controller.login = function () {
        $http({
            method: 'POST',
            url: 'api/login',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: $httpParamSerializer(controller.formData)
        }).then(
            function success(response) {
                user.update('/rooms');
            },
            function error(response) {
                controller.formError = "Invalid username or password.";
            }
        );
    };
});
