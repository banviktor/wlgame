angular.module('WLGame').controller('RoomController', function ($http, $routeParams, user) {
    var controller = this;

    controller.room = {
        _links: {self: {href: 'api/rooms/' + $routeParams.id}}
    };
    controller.players = [];
    controller.readyPlayersCount = 0;
    controller.player = {};
    controller.wordsToUpload = [];
    controller.translationsToUpload = [];

    controller.refresh = function () {
        $http.get(controller.room._links.self.href).then(
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
        var readyPlayers = 0;
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
            if (player.ready) {
                ++readyPlayers;
            }

            if (player.self) {
                controller.players.unshift(player);
                controller.player = player;
            } else {
                controller.players.push(player);
            }
        }
        controller.readyPlayersCount = readyPlayers;
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
    controller.uploadWords = function () {
        var wordMap = {};
        for (var i = 0; i < controller.wordsToUpload.length; ++i) {
            wordMap[controller.wordsToUpload[i]] = controller.translationsToUpload[i];
        }
        $http.post(controller.room._links.upload_words.href, wordMap).then(
            function success () {
                controller.refresh();
            }
        )
    };

    controller.refresh();
    controller.refresher = setInterval(controller.refresh, 3000);
});
