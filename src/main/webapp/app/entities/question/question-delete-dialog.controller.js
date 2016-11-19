(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('QuestionDeleteController',QuestionDeleteController);

    QuestionDeleteController.$inject = ['$uibModalInstance', 'entity', 'Question'];

    function QuestionDeleteController($uibModalInstance, entity, Question) {
        var vm = this;
        vm.question = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Question.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
