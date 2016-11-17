(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('QuestionDialogController', QuestionDialogController);

    QuestionDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Question', 'Topic', 'Proposition', 'Quiz'];

    function QuestionDialogController ($scope, $stateParams, $uibModalInstance, entity, Question, Topic, Proposition, Quiz) {
        var vm = this;
        vm.question = entity;
        vm.topics = Topic.query();
        vm.propositions = Proposition.query();
        vm.quizzes = Quiz.query();

        var onSaveSuccess = function (result) {
            $scope.$emit('quizApp:questionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.question.id !== null) {
                Question.update(vm.question, onSaveSuccess, onSaveError);
            } else {
                Question.save(vm.question, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
