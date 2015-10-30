'use strict';

/**
 * @ngdoc function
 * @name yapp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of yapp
 */
angular.module('yapp')
        .controller('LoginCtrl', function ($scope, $location, md5, AuthenticationService, $cookieStore) {

            $cookieStore.remove('globals');

            $scope.email = '';
            $scope.senha = '';
            $scope.hash = '';

            $scope.$watch('email', function () {
                $scope.hash = md5.createHash($scope.email);
            });

            $scope.submit = function () {

                var hashSenha = md5.createHash($scope.senha);
                AuthenticationService.Login($scope.email, hashSenha, function (response) {
                    if (response.sucesso) {
                        AuthenticationService.SetCredentials($scope.email, hashSenha, response.nome);
                        $location.path('/dashboard');
                    } else {
                        alert(response.mensagem);
                    }
                });

                return false;
            }

        });
