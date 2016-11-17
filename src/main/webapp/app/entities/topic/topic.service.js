(function() {
    'use strict';
    angular
        .module('quizApp')
        .factory('Topic', Topic);

    Topic.$inject = ['$resource'];

    function Topic ($resource) {
        var resourceUrl =  'api/topics/:id';

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
