(function() {
    'use strict';

    angular
        .module('quizApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('question', {
            parent: 'entity',
            url: '/question?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'quizApp.question.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/question/questions.html',
                    controller: 'QuestionController',
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
                    $translatePartialLoader.addPart('question');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('question-detail', {
            parent: 'entity',
            url: '/question/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'quizApp.question.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/question/question-detail.html',
                    controller: 'QuestionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('question');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Question', function($stateParams, Question) {
                    return Question.get({id : $stateParams.id});
                }]
            }
        })
        .state('question.new', {
            parent: 'question',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/question/question-dialog.html',
                    controller: 'QuestionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                statement: null,
                                level: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('question', null, { reload: true });
                }, function() {
                    $state.go('question');
                });
            }]
        })
        .state('question.edit', {
            parent: 'question',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/question/question-dialog.html',
                    controller: 'QuestionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Question', function(Question) {
                            return Question.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('question', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('question.delete', {
            parent: 'question',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/question/question-delete-dialog.html',
                    controller: 'QuestionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Question', function(Question) {
                            return Question.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('question', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
