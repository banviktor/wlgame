angular.module('WLGame').controller('RoomController', function ($http, $routeParams, user) {
    var controller = this;

    controller.room = {
        _links: {
            self: 'api/rooms/' + $routeParams.id
        }
    };
    controller.players = [];
    controller.connectedPlayers = 0;

    controller.refresh = function () {
        $http.get(controller.room._links.self).then(
            function success (response) {
                controller.room = response.data;
                if (controller.room.state == 'ENDED') {
                    controller.roomEnded();
                } else {
                    controller.refreshPlayers();
                }
            }
        );
    };
    controller.refreshPlayers = function () {
        controller.players = [];
        for (var i = 0; i < controller.room.roomPlayers.length; ++i) {
            var player = {
                placeholder: false,
                name: controller.room.roomPlayers[i].playerName,
                uploadedWords: controller.room.roomPlayers[i].uploadedWords,
                uploadedSolutions: controller.room.roomPlayers[i].uploadedSolutions,
                self: controller.room.roomPlayers[i].playerName == user.user.name
            };
            switch (controller.room.state) {
                case 'ENDED':
                case 'WAITING_FOR_PLAYERS':
                    player.ready = true;
                    break;

                case 'WAITING_FOR_WORDS':
                    player.ready = player.uploadedWords;
                    break;

                case 'WAITING_FOR_SOLUTIONS':
                    player.ready = player.uploadedSolutions;
                    break;

                default:
                    player.ready = false;
            }
            if (player.self) {
                controller.players.unshift(player);
            } else {
                controller.players.push(player);
            }
        }
        controller.connectedPlayers = controller.players.length;
        for (var j = controller.players.length; j < controller.room.maxPlayers; ++j) {
            controller.players.push({
                placeholder: true,
                self: false
            });
        }
    };
    controller.roomEnded = function () {
        clearInterval(controller.refresher);
    };

    controller.refresher = setInterval(controller.refresh(), 3000);
});
