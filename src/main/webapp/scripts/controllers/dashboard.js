'use strict';

/**
 * @ngdoc function
 * @name yapp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of yapp
 */
angular.module('yapp')
        .controller('DashboardCtrl', function ($scope, $state, $rootScope, $cookieStore, md5, $http) {

            $scope.$state = $state;

            $rootScope.globals = $cookieStore.get('globals') || {};
            $scope.hash = md5.createHash($rootScope.globals.currentUser.email);

            $scope.recursos = [];

            $http.get('api/recursos')
                    .success(function (response) {
                        $scope.recursos = response;
                    });

        });
