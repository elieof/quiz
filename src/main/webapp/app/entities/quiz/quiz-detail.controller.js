(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('QuizDetailController', QuizDetailController);

    QuizDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Quiz', 'Question'];

    function QuizDetailController($scope, $rootScope, $stateParams, entity, Quiz, Question) {
        var vm = this;
        vm.quiz = entity;
        
        var unsubscribe = $rootScope.$on('quizApp:quizUpdate', function(event, result) {
            vm.quiz = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
