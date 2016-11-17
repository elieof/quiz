(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('QuizDialogController', QuizDialogController);

    QuizDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Quiz', 'Question'];

    function QuizDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Quiz, Question) {
        var vm = this;

        vm.quiz = entity;
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
            if (vm.quiz.id !== null) {
                Quiz.update(vm.quiz, onSaveSuccess, onSaveError);
            } else {
                Quiz.save(vm.quiz, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('quizApp:quizUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
