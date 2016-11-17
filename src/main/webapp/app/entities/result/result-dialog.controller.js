(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('ResultDialogController', ResultDialogController);

    ResultDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Result', 'Quiz', 'User', 'Question', 'Proposition'];

    function ResultDialogController ($scope, $stateParams, $uibModalInstance, entity, Result, Quiz, User, Question, Proposition) {
        var vm = this;
        vm.result = entity;
        vm.quizzes = Quiz.query();
        vm.users = User.query();
        vm.questions = Question.query();
        vm.propositions = Proposition.query();

        var onSaveSuccess = function (result) {
            $scope.$emit('quizApp:resultUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.result.id !== null) {
                Result.update(vm.result, onSaveSuccess, onSaveError);
            } else {
                Result.save(vm.result, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
