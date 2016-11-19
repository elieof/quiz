(function() {
    'use strict';

    angular
        .module('quizApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('proposition', {
            parent: 'entity',
            url: '/proposition?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'quizApp.proposition.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/proposition/propositions.html',
                    controller: 'PropositionController',
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
                    $translatePartialLoader.addPart('proposition');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('proposition-detail', {
            parent: 'entity',
            url: '/proposition/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'quizApp.proposition.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/proposition/proposition-detail.html',
                    controller: 'PropositionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('proposition');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Proposition', function($stateParams, Proposition) {
                    return Proposition.get({id : $stateParams.id});
                }]
            }
        })
        .state('proposition.new', {
            parent: 'proposition',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/proposition/proposition-dialog.html',
                    controller: 'PropositionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                statement: null,
                                valid: false,
                                explanation: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('proposition', null, { reload: true });
                }, function() {
                    $state.go('proposition');
                });
            }]
        })
        .state('proposition.edit', {
            parent: 'proposition',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/proposition/proposition-dialog.html',
                    controller: 'PropositionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Proposition', function(Proposition) {
                            return Proposition.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('proposition', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('proposition.delete', {
            parent: 'proposition',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/proposition/proposition-delete-dialog.html',
                    controller: 'PropositionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Proposition', function(Proposition) {
                            return Proposition.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('proposition', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();