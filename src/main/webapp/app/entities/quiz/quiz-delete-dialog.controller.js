(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('QuizDeleteController',QuizDeleteController);

    QuizDeleteController.$inject = ['$uibModalInstance', 'entity', 'Quiz'];

    function QuizDeleteController($uibModalInstance, entity, Quiz) {
        var vm = this;

        vm.quiz = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Quiz.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
