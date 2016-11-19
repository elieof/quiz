(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('TopicDeleteController',TopicDeleteController);

    TopicDeleteController.$inject = ['$uibModalInstance', 'entity', 'Topic'];

    function TopicDeleteController($uibModalInstance, entity, Topic) {
        var vm = this;
        vm.topic = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Topic.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
