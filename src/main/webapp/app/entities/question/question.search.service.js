(function() {
    'use strict';

    angular
        .module('quizApp')
        .factory('QuestionSearch', QuestionSearch);

    QuestionSearch.$inject = ['$resource'];

    function QuestionSearch($resource) {
        var resourceUrl =  'api/_search/questions/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
