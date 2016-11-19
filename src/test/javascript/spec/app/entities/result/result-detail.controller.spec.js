'use strict';

describe('Controller Tests', function() {

    describe('Result Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockResult, MockQuiz, MockUser, MockQuestion, MockProposition;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockResult = jasmine.createSpy('MockResult');
            MockQuiz = jasmine.createSpy('MockQuiz');
            MockUser = jasmine.createSpy('MockUser');
            MockQuestion = jasmine.createSpy('MockQuestion');
            MockProposition = jasmine.createSpy('MockProposition');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Result': MockResult,
                'Quiz': MockQuiz,
                'User': MockUser,
                'Question': MockQuestion,
                'Proposition': MockProposition
            };
            createController = function() {
                $injector.get('$controller')("ResultDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'quizApp:resultUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
