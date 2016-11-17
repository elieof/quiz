(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('PropositionDetailController', PropositionDetailController);

    PropositionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Proposition', 'Question'];

    function PropositionDetailController($scope, $rootScope, $stateParams, previousState, entity, Proposition, Question) {
        var vm = this;

        vm.proposition = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('quizApp:propositionUpdate', function(event, result) {
            vm.proposition = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
