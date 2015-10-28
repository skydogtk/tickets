'use strict';

/**
 * @ngdoc overview
 * @name yapp
 * @description
 * # yapp
 *
 * Main module of the application.
 */
angular
        .module('yapp', [
            'ui.router',
            'ngAnimate'
        ])
        .config(function ($stateProvider, $urlRouterProvider) {

            $urlRouterProvider.when('/dashboard', '/dashboard/chamados');
            $urlRouterProvider.otherwise('/login');

            $stateProvider
                    .state('base', {
                        abstract: true,
                        url: '',
                        templateUrl: 'views/base.html'
                    })
                    .state('login', {
                        url: '/login',
                        parent: 'base',
                        templateUrl: 'views/login.html',
                        controller: 'LoginCtrl'
                    })
                    .state('dashboard', {
                        url: '/dashboard',
                        parent: 'base',
                        templateUrl: 'views/dashboard.html',
                        controller: 'DashboardCtrl'
                    })
                    .state('chamados', {
                        url: '/chamados',
                        parent: 'dashboard',
                        templateUrl: 'views/dashboard/chamados.html'
                    })
                    .state('usuarios', {
                        url: '/usuarios',
                        parent: 'dashboard',
                        templateUrl: 'views/dashboard/usuarios  .html'
                    })
                    .state('relatorios', {
                        url: '/relatorios',
                        parent: 'dashboard',
                        templateUrl: 'views/dashboard/relatorios.html'
                    });

        });
