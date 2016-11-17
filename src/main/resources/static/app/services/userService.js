angular.module('WLGame').factory('user', function ($location) {
    var service = {};

    service.user = null;
    service.isLoggedIn = function () {
        return this.user != null;
    };
    service.redirectToLogin = function () {
        $location.path('/login');
    };

    return service;
});
