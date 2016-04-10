(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('PropositionDetailController', PropositionDetailController);

    PropositionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Proposition', 'Question'];

    function PropositionDetailController($scope, $rootScope, $stateParams, entity, Proposition, Question) {
        var vm = this;
        vm.proposition = entity;
        
        var unsubscribe = $rootScope.$on('quizApp:propositionUpdate', function(event, result) {
            vm.proposition = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
