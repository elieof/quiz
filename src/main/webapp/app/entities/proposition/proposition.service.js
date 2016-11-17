(function() {
    'use strict';
    angular
        .module('quizApp')
        .factory('Proposition', Proposition);

    Proposition.$inject = ['$resource'];

    function Proposition ($resource) {
        var resourceUrl =  'api/propositions/:id';

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
