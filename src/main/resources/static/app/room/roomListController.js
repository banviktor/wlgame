angular.module('WLGame').controller('RoomListController', function ($http, $location) {
    var controller = this;

    controller.rooms = [];
    controller.newRoom = {
        languageFrom: null,
        languageTo: null,
        maxPlayers: 2
    };
    controller.languages = [];

    controller.fetchLanguages = function () {
        $http.get('api/languages').then(
            function success (response) {
                controller.languages = response.data;
                controller.newRoom.languageFrom = controller.languages[0].id;
                controller.newRoom.languageTo = controller.languages[1].id;
            }
        );
    };
    controller.fetchRooms = function () {
        $http.get('api/rooms').then(
            function success (response) {
                if (response.data.hasOwnProperty('_embedded')){
                    controller.rooms = response.data._embedded.rooms;
                } else {
                    controller.rooms = [];
                }
            }
        );
    };
    controller.joinRoom = function (room) {
        $http.post(room._links.join.href, {}).then(
            function success () {
                $location.path('rooms/' + room.id);
            },
            function error() {
                alert('Failed to join room.');
            }
        );
    };
    controller.createRoom = function () {
        $http.post('api/rooms', controller.newRoom).then(
            function success (response) {
                $location.path('rooms/' + response.data.id);
            },
            function error () {
                alert('Failed to create room.');
            }
        );
    };

    controller.fetchLanguages();
    setInterval(controller.fetchRooms, 3000);
});
