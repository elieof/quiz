(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('PropositionDialogController', PropositionDialogController);

    PropositionDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Proposition', 'Question'];

    function PropositionDialogController ($scope, $stateParams, $uibModalInstance, entity, Proposition, Question) {
        var vm = this;
        vm.proposition = entity;
        vm.questions = Question.query();
        vm.load = function(id) {
            Proposition.get({id : id}, function(result) {
                vm.proposition = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('quizApp:propositionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.proposition.id !== null) {
                Proposition.update(vm.proposition, onSaveSuccess, onSaveError);
            } else {
                Proposition.save(vm.proposition, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
