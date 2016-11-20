angular.module('WLGame').controller('RoomListController', function ($http, $location, user, refreshers) {
    var controller = this;

    controller.rooms = [];
    controller.languages = [];
    controller.newRoom = {
        languageFrom: null,
        languageTo: null,
        maxPlayers: 2
    };

    controller.fetchRooms = function () {
        $http.get('api/rooms').then(
            function success (response) {
                if (response.data.hasOwnProperty('_embedded')){
                    controller.rooms = response.data._embedded.rooms;
                } else {
                    controller.rooms = [];
                }
            },
            user.handleUnauthenticated()
        );
    };
    controller.fetchLanguages = function () {
        $http.get('api/languages').then(
            function success (response) {
                controller.languages = response.data;
                controller.newRoom.languageFrom = controller.languages[0].id;
                controller.newRoom.languageTo = controller.languages[1].id;
            },
            user.handleUnauthenticated()
        );
    };
    controller.joinRoom = function (room) {
        $http.post(room._links.join.href, {}).then(
            function success () {
                controller.redirectToRoom(room.id);
            },
            user.handleUnauthenticated()
        );
    };
    controller.createRoom = function () {
        $http.post('api/rooms', controller.newRoom).then(
            function success (response) {
                controller.redirectToRoom(response.data.id);
            },
            user.handleUnauthenticated()
        );
    };
    controller.redirectToRoom = function (roomID) {
        $location.path('rooms/' + roomID);
    };

    controller.fetchLanguages();
    refreshers.add('room', controller.fetchRooms, 1000, true);
});
