(function() {
    'use strict';

    angular
        .module('quizApp')
        .factory('QuizSearch', QuizSearch);

    QuizSearch.$inject = ['$resource'];

    function QuizSearch($resource) {
        var resourceUrl =  'api/_search/quizzes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
