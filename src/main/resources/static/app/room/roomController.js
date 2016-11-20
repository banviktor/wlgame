angular.module('WLGame').controller('RoomController', function ($http, $routeParams, user, refreshers) {
    var controller = this;

    controller.room = {
        _links: {self: {href: 'api/rooms/' + $routeParams.id}}
    };
    controller.players = [];
    controller.readyPlayersCount = 0;
    controller.player = {
        winner: false,
        placeholder: false,
        ready: false,
        self: true
    };
    controller.wordsToUpload = [];
    controller.translationsToUpload = [];
    controller.words = [];
    controller.solutionsToUpload = {
        uninitialized: true
    };
    controller.evaluation = {
        uninitialized: true
    };

    controller.refresh = function () {
        $http.get(controller.room._links.self.href).then(
            function success (response) {
                controller.refreshRoom(response.data);
            },
            user.handleUnauthenticated()
        );
    };
    controller.refreshRoom = function (room) {
        controller.room = room;
        controller.refreshPlayers();
        controller.refreshState();
    };
    controller.refreshPlayers = function () {
        controller.players = [];
        var readyPlayers = 0;
        for (var i = 0; i < controller.room.roomPlayers.length; ++i) {
            var player = {
                winner: false,
                placeholder: false,
                name: controller.room.roomPlayers[i].playerName,
                state: controller.room.roomPlayers[i].state,
                self: controller.room.roomPlayers[i].playerName == user.user.name
            };
            switch (controller.room.state) {
                case 'ENDED':
                case 'WAITING_FOR_PLAYERS':
                    player.ready = true;
                    break;

                case 'WAITING_FOR_WORDS':
                    player.ready = player.state == 'WAITING_FOR_ROOM';
                    break;

                case 'IN_PROGRESS':
                    player.ready = player.state == 'DONE';
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
    controller.refreshState = function () {
        if (controller.room.state == 'ENDED') {
            controller.roomEnded();
        }
        if (controller.player.state == 'MEMORIZING' && controller.words.length == 0) {
            $http.get(controller.room._links.words.href).then(
                function success (response) {
                    for (var i = 0; i < response.data.length; ++i) {
                        controller.words.push({
                            id: response.data[i].id,
                            word: response.data[i].word,
                            translations: []
                        });
                    }
                    $http.get(controller.room._links.translations.href).then(
                        function success (response) {
                            for (var i = 0; i < controller.words.length; ++i) {
                                controller.words[i].translations = response.data[controller.words[i].id];
                            }
                        },
                        user.handleUnauthenticated()
                    );
                },
                user.handleUnauthenticated()
            );
        }
        if (controller.player.state == 'SOLVING' && controller.solutionsToUpload.hasOwnProperty('uninitialized')) {
            delete controller.solutionsToUpload.uninitialized;
            for (var i = 0; i < controller.words.length; ++i) {
                controller.solutionsToUpload[controller.words[i].id] = '';
            }
        }
    };
    controller.roomEnded = function () {
        refreshers.remove('room');
        if (controller.player.state == 'DONE') {
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
            },
            user.handleUnauthenticated()
        )
    };
    controller.startMemorizing = function () {
        $http.post(controller.room._links.start_memorizing.href, {}).then(
            function success (response) {
                controller.refreshRoom(response.data);
            },
            user.handleUnauthenticated()
        );
    };
    controller.startSolving = function () {
        $http.post(controller.room._links.start_solving.href, {}).then(
            function success (response) {
                controller.refreshRoom(response.data);
            },
            user.handleUnauthenticated()
        );
    };
    controller.uploadSolutions = function () {
        $http.post(controller.room._links.upload_solutions.href, controller.solutionsToUpload).then(
            function success () {
                controller.refresh();
            },
            user.handleUnauthenticated()
        )
    };
    controller.evaluate = function () {
        $http.get(controller.room._links.solutions.href).then(
            function (response) {
                controller.evaluation.numCorrect = 0;
                controller.evaluation.numIncorrect = 0;
                controller.evaluation.numSolutions = response.data.length;
                controller.evaluation.mistakes = [];
                for (var i = 0; i < response.data.length; ++i) {
                    var solution = response.data[i];
                    if (solution.correct) {
                        ++controller.evaluation.numCorrect;
                    } else {
                        ++controller.evaluation.numIncorrect;
                        controller.evaluation.mistakes.push({
                            word: solution.word.word,
                            expected: solution.expected.join('; '),
                            input: solution.input
                        });
                    }
                }
                controller.evaluation.uninitialized = false;
                console.log(controller.evaluation);
            },
            user.handleUnauthenticated()
        );
    };

    refreshers.add('room', controller.refresh, 1000, true);
});
