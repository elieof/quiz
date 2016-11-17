(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('ResultDialogController', ResultDialogController);

    ResultDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Result', 'Quiz', 'User', 'Question', 'Proposition'];

    function ResultDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Result, Quiz, User, Question, Proposition) {
        var vm = this;

        vm.result = entity;
        vm.clear = clear;
        vm.save = save;
        vm.quizzes = Quiz.query();
        vm.users = User.query();
        vm.questions = Question.query();
        vm.propositions = Proposition.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.result.id !== null) {
                Result.update(vm.result, onSaveSuccess, onSaveError);
            } else {
                Result.save(vm.result, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('quizApp:resultUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
