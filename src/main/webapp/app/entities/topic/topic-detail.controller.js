(function() {
    'use strict';

    angular
        .module('quizApp')
        .controller('TopicDetailController', TopicDetailController);

    TopicDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Topic'];

    function TopicDetailController($scope, $rootScope, $stateParams, previousState, entity, Topic) {
        var vm = this;

        vm.topic = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('quizApp:topicUpdate', function(event, result) {
            vm.topic = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
