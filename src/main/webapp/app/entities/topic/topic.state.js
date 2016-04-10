(function() {
    'use strict';

    angular
        .module('quizApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('topic', {
            parent: 'entity',
            url: '/topic?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'quizApp.topic.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/topic/topics.html',
                    controller: 'TopicController',
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
                    $translatePartialLoader.addPart('topic');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('topic-detail', {
            parent: 'entity',
            url: '/topic/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'quizApp.topic.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/topic/topic-detail.html',
                    controller: 'TopicDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('topic');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Topic', function($stateParams, Topic) {
                    return Topic.get({id : $stateParams.id});
                }]
            }
        })
        .state('topic.new', {
            parent: 'topic',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/topic/topic-dialog.html',
                    controller: 'TopicDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('topic', null, { reload: true });
                }, function() {
                    $state.go('topic');
                });
            }]
        })
        .state('topic.edit', {
            parent: 'topic',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/topic/topic-dialog.html',
                    controller: 'TopicDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Topic', function(Topic) {
                            return Topic.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('topic', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('topic.delete', {
            parent: 'topic',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/topic/topic-delete-dialog.html',
                    controller: 'TopicDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Topic', function(Topic) {
                            return Topic.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('topic', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
