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
            'ngAnimate',
            'ngCookies',
            'ab-base64',
            'angular-md5',
            'ngTable',
            'angularModalService'
        ])

        .factory('AuthenticationService',
                ['$http', '$cookieStore', '$rootScope', '$timeout', 'base64',
                    function ($http, $cookieStore, $rootScope, $timeout, base64) {
                        var service = {};

                        service.Login = function (email, senha, callback) {
                            $http.post('api/auth', {email: email, senha: senha})
                                    .success(function (response) {
                                        callback(response);
                                    });
                        };
                        ;

                        service.SetCredentials = function (email, senha, nome, papel) {
                            var authdata = base64.encode(email + ':' + senha);

                            $rootScope.globals = {
                                currentUser: {
                                    nome: nome,
                                    email: email,
                                    authdata: authdata,
                                    papel: papel
                                }
                            };

                            $http.defaults.headers.common['Authorization'] = 'Basic ' + authdata;
                            $cookieStore.put('globals', $rootScope.globals);
                        };

                        service.ClearCredentials = function () {
                            $rootScope.globals = {};
                            $cookieStore.remove('globals');
                            $http.defaults.headers.common.Authorization = 'Basic ';
                        };

                        return service;
                    }])

        .run(["$rootScope", "$location", "$window", "AuthenticationService", '$cookieStore', '$http', function ($rootScope, $location, $window, AuthenticationService, $cookieStore, $http) {
                $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams, $state) {
                    $rootScope.globals = $cookieStore.get('globals') || {};
                    if ($rootScope.globals.currentUser) {
                        $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata;
                    }
                    if (window.location.href.indexOf("login") === -1 && !$rootScope.globals.currentUser) {
                        event.preventDefault();
                        $location.path('/login');
                    }
                });
            }])

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
                        templateUrl: 'views/dashboard/chamados.html',
                        controller: 'ChamadosCtrl'
                    })
                    .state('relatorios', {
                        url: '/relatorios',
                        parent: 'dashboard',
                        templateUrl: 'views/dashboard/relatorios.html',
                        controller: 'RelatoriosCtrl'
                    })
                    .state('usuarios', {
                        url: '/usuarios',
                        parent: 'dashboard',
                        templateUrl: 'views/dashboard/usuarios.html',
                        controller: 'UsuariosCtrl'
                    });
        });
