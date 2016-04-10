'use strict';

describe('Controller Tests', function() {

    describe('Quiz Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockQuiz, MockQuestion;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockQuiz = jasmine.createSpy('MockQuiz');
            MockQuestion = jasmine.createSpy('MockQuestion');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Quiz': MockQuiz,
                'Question': MockQuestion
            };
            createController = function() {
                $injector.get('$controller')("QuizDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'quizApp:quizUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
