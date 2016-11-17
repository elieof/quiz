(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('PropositionDeleteController',PropositionDeleteController);

    PropositionDeleteController.$inject = ['$uibModalInstance', 'entity', 'Proposition'];

    function PropositionDeleteController($uibModalInstance, entity, Proposition) {
        var vm = this;

        vm.proposition = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Proposition.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
