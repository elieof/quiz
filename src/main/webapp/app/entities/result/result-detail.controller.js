(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('ResultDetailController', ResultDetailController);

    ResultDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Result', 'Quiz', 'User', 'Question', 'Proposition'];

    function ResultDetailController($scope, $rootScope, $stateParams, entity, Result, Quiz, User, Question, Proposition) {
        var vm = this;
        vm.result = entity;
        vm.load = function (id) {
            Result.get({id: id}, function(result) {
                vm.result = result;
            });
        };
        var unsubscribe = $rootScope.$on('quizApp:resultUpdate', function(event, result) {
            vm.result = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
