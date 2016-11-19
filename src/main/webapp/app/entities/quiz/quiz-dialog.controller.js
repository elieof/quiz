(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('QuizDialogController', QuizDialogController);

    QuizDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Quiz', 'Question'];

    function QuizDialogController ($scope, $stateParams, $uibModalInstance, entity, Quiz, Question) {
        var vm = this;
        vm.quiz = entity;
        vm.questions = Question.query();
        vm.load = function(id) {
            Quiz.get({id : id}, function(result) {
                vm.quiz = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('quizApp:quizUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.quiz.id !== null) {
                Quiz.update(vm.quiz, onSaveSuccess, onSaveError);
            } else {
                Quiz.save(vm.quiz, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
