angular.module('WLGame').controller('RoomController', function ($http, $routeParams, user) {
    var controller = this;

    controller.room = {
        _links: {self: {href: 'api/rooms/' + $routeParams.id}}
    };
    controller.players = [];
    controller.readyPlayersCount = 0;
    controller.player = {
        winner: false,
        placeholder: false,
        uploadedWords: false,
        uploadedSolutions: false,
        self: true
    };
    controller.wordsToUpload = [];
    controller.translationsToUpload = [];
    controller.words = [];
    controller.solutionsToUpload = {};
    controller.evaluation = {
        done: false
    };

    controller.refresh = function () {
        $http.get(controller.room._links.self.href).then(
            function success (response) {
                controller.room = response.data;
                if (controller.room.state == 'ENDED') {
                    controller.roomEnded();
                }
                if (controller.words.length == 0 && controller.room.state == 'IN_PROGRESS') {
                    controller.words = controller.room.words;
                    for (var i = 0; i < controller.room.words.length; ++i) {
                        if (!controller.solutionsToUpload.hasOwnProperty(controller.room.words[i].id)) {
                            controller.solutionsToUpload[controller.room.words[i].id] = '';
                        }
                    }
                }
                controller.refreshPlayers();
            }
        );
    };
    controller.refreshPlayers = function () {
        controller.players = [];
        var readyPlayers = 0;
        for (var i = 0; i < controller.room.roomPlayers.length; ++i) {
            var player = {
                winner: false,
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

                case 'IN_PROGRESS':
                    player.ready = player.uploadedSolutions;
                    break;

                default:
                    player.ready = false;
            }
            if (player.ready) {
                ++readyPlayers;
            }
            player.winner = controller.room.winners.indexOf(player.name) != -1;
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
        if (controller.room.solutions.length > 0) {
            controller.evaluate();
        }
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
    controller.uploadSolutions = function () {
        $http.post(controller.room._links.upload_solutions.href, controller.solutionsToUpload).then(
            function success () {
                controller.refresh();
            }
        )
    };
    controller.evaluate = function () {
        controller.evaluation.numCorrect = 0;
        controller.evaluation.numIncorrect = 0;
        controller.evaluation.numSolutions = controller.room.solutions.length;
        controller.evaluation.mistakes = [];
        for (var i = 0; i < controller.room.solutions.length; ++i) {
            var solution = controller.room.solutions[i];
            if (solution.correct) {
                ++controller.evaluation.numCorrect;
            } else {
                ++controller.evaluation.numIncorrect;
                controller.evaluation.mistakes.push({
                    word: solution.word.word,
                    expected: solution.expected,
                    input: solution.input
                });
            }
        }
        controller.evaluation.done = true;
    };

    controller.refresh();
    controller.refresher = setInterval(controller.refresh, 3000);
});
