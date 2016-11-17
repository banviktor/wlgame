angular.module('WLGame').config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/login', {
            templateUrl: 'app/login/loginView.html',
            controller: 'LoginController',
            controllerAs: 'loginCtrl'
        })
        .when('/register', {
            templateUrl: 'app/login/registrationView.html',
            controller: 'RegistrationController',
            controllerAs: 'registrationCtrl'
        })
        .when('/rooms', {
            templateUrl: 'app/room/roomListView.html',
            controller: 'RoomListController',
            controllerAs: 'roomListCtrl'
        })
        .when('/rooms/:id', {
            templateUrl: 'app/rooom/roomView.html',
            controller: 'RoomController',
            controllerAs: 'roomCtrl'
        })
        .otherwise({
            redirectTo: '/rooms'
        });
}]);
