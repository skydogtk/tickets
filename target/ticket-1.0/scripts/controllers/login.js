'use strict';

/**
 * @ngdoc function
 * @name yapp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of yapp
 */
angular.module('yapp')
        .controller('LoginCtrl', function ($scope, $location, md5, AuthenticationService) {

            $scope.email = '';
            $scope.senha = '';
            $scope.hash = '';

            $scope.submit = function () {

                var hashSenha = md5.createHash($scope.senha);
                AuthenticationService.Login($scope.email, hashSenha, function (response) {
                    if (response.sucesso) {
                        AuthenticationService.SetCredentials($scope.email, hashSenha, response.nome);
                        $location.path('/dashboard');
//                        $window.location.href = "index.html";
                    } else {
//                        $('span', '.alert-danger', $('.login-form')).text(response.mensagem);
//                        $('.alert-danger', $('.login-form')).show();
                    }
                });

                return false;
            }

        });
