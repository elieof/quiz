'use strict';

describe('Controller Tests', function() {

    describe('Question Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockQuestion, MockTopic, MockProposition, MockQuiz;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockQuestion = jasmine.createSpy('MockQuestion');
            MockTopic = jasmine.createSpy('MockTopic');
            MockProposition = jasmine.createSpy('MockProposition');
            MockQuiz = jasmine.createSpy('MockQuiz');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Question': MockQuestion,
                'Topic': MockTopic,
                'Proposition': MockProposition,
                'Quiz': MockQuiz
            };
            createController = function() {
                $injector.get('$controller')("QuestionDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'quizApp:questionUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
