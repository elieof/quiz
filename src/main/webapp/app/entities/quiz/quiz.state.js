(function() {
    'use strict';

    angular
        .module('quizApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('quiz', {
            parent: 'entity',
            url: '/quiz?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'quizApp.quiz.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/quiz/quizzes.html',
                    controller: 'QuizController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('quiz');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('quiz-detail', {
            parent: 'entity',
            url: '/quiz/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'quizApp.quiz.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/quiz/quiz-detail.html',
                    controller: 'QuizDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('quiz');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Quiz', function($stateParams, Quiz) {
                    return Quiz.get({id : $stateParams.id});
                }]
            }
        })
        .state('quiz.new', {
            parent: 'quiz',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quiz/quiz-dialog.html',
                    controller: 'QuizDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('quiz', null, { reload: true });
                }, function() {
                    $state.go('quiz');
                });
            }]
        })
        .state('quiz.edit', {
            parent: 'quiz',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quiz/quiz-dialog.html',
                    controller: 'QuizDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Quiz', function(Quiz) {
                            return Quiz.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('quiz', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('quiz.delete', {
            parent: 'quiz',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quiz/quiz-delete-dialog.html',
                    controller: 'QuizDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Quiz', function(Quiz) {
                            return Quiz.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('quiz', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
