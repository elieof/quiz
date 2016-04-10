(function() {
    'use strict';

    angular
        .module('quizApp')
        .factory('TopicSearch', TopicSearch);

    TopicSearch.$inject = ['$resource'];

    function TopicSearch($resource) {
        var resourceUrl =  'api/_search/topics/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
