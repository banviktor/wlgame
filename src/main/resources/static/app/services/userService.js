angular.module('WLGame').factory('user', function ($location, $route, $http, refreshers) {
    var service = {};

    service.user = null;

    service.update = function (redirectTo) {
        $http.get('api/self').then(
            function success(response) {
                service.user = response.data;
                if (redirectTo !== undefined) {
                    $location.path(redirectTo);
                }
            },
            function error() {
                var route = $route.routes[$location.path()];
                if (route === undefined || (route.controller != 'LoginController' && route.controller != 'RegistrationController')) {
                    $location.path('/login');
                    refreshers.clear();
                    service.setUpAutomaticRefresh();
                }
            }
        );
    };
    service.handleUnauthenticated = function (customCallback, onUnauthenticated) {
        return function (response) {
            if (response.status == 401) {
                service.update();
                if (onUnauthenticated !== undefined) {
                    onUnauthenticated(response);
                }
            } else if (customCallback !== undefined) {
                customCallback(response);
            }
        }
    };
    service.setUpAutomaticRefresh = function () {
        refreshers.add('user', service.update, 15000, true);
    };

    service.setUpAutomaticRefresh();
    return service;
});
