angular.module('WLGame').controller('RegistrationController', function ($http, $scope, user) {
    var controller = this;

    controller.formData = {
        name: '',
        password: '',
        password_confirm: ''
    };
    controller.formError = null;

    controller.register = function () {
        if (controller.formData.password != controller.formData.password_confirm) {
            controller.formData.password = '';
            controller.formData.password_confirm = '';
            $scope.register.pass.$setValidity("nomatch", false);
            $scope.register.pass_confirm.$setValidity("nomatch", false);
            controller.formError = "The passwords don't match.";
            return;
        }
        $http.post('api/users', controller.formData).then(
            function success () {
                user.update('/rooms');
            },
            function error () {
                controller.formError = "Something went wrong. Please try again later.";
            }
        );
    }
});
