angular.module('WLGame')
    .filter('ucfirst', function () {
        return function (input) {
            return input.charAt(0).toUpperCase() + input.substr(1);
        }
    })
;
