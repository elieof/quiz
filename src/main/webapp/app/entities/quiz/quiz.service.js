(function() {
    'use strict';
    angular
        .module('quizApp')
        .factory('Quiz', Quiz);

    Quiz.$inject = ['$resource'];

    function Quiz ($resource) {
        var resourceUrl =  'api/quizzes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
