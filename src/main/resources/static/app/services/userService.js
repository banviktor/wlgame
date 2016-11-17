angular.module('WLGame').factory('user', [function () {
    var service = {};

    service.user = null;
    service.isLoggedIn = function () {
        return this.user != null;
    };

    return service;
}]);
