(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('PropositionDetailController', PropositionDetailController);

    PropositionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Proposition', 'Question'];

    function PropositionDetailController($scope, $rootScope, $stateParams, entity, Proposition, Question) {
        var vm = this;
        vm.proposition = entity;
        vm.load = function (id) {
            Proposition.get({id: id}, function(result) {
                vm.proposition = result;
            });
        };
        var unsubscribe = $rootScope.$on('quizApp:propositionUpdate', function(event, result) {
            vm.proposition = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
