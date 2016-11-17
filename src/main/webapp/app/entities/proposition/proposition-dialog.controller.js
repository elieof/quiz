(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('PropositionDialogController', PropositionDialogController);

    PropositionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Proposition', 'Question'];

    function PropositionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Proposition, Question) {
        var vm = this;

        vm.proposition = entity;
        vm.clear = clear;
        vm.save = save;
        vm.questions = Question.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.proposition.id !== null) {
                Proposition.update(vm.proposition, onSaveSuccess, onSaveError);
            } else {
                Proposition.save(vm.proposition, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('quizApp:propositionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
