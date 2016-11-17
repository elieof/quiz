(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('TopicDeleteController',TopicDeleteController);

    TopicDeleteController.$inject = ['$uibModalInstance', 'entity', 'Topic'];

    function TopicDeleteController($uibModalInstance, entity, Topic) {
        var vm = this;

        vm.topic = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Topic.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
