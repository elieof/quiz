(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('TopicDialogController', TopicDialogController);

    TopicDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Topic'];

    function TopicDialogController ($scope, $stateParams, $uibModalInstance, entity, Topic) {
        var vm = this;
        vm.topic = entity;
        vm.load = function(id) {
            Topic.get({id : id}, function(result) {
                vm.topic = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('quizApp:topicUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.topic.id !== null) {
                Topic.update(vm.topic, onSaveSuccess, onSaveError);
            } else {
                Topic.save(vm.topic, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
