angular.module('WLGame').factory('user', function ($location, $http) {
    var service = {};

    service.user = null;
    service.isLoggedIn = function () {
        return service.user != null;
    };
    service.redirectToLogin = function () {
        $location.path('/login');
    };
    service.requireLogin = function () {
        if (!service.isLoggedIn()) {
            service.redirectToLogin();
        }
    };
    service.update = function (redirectTo) {
        $http.get('api/self').then(
            function success(response) {
                service.user = response.data;
                if (redirectTo !== undefined) {
                    $location.path(redirectTo);
                }
            },
            function error(response) {
                service.user = null;
                if (redirectTo !== undefined) {
                    $location.path(redirectTo);
                }
            }
        );
    };
    service.update($location.path());

    return service;
});
