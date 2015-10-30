'use strict';

/**
 * @ngdoc function
 * @name yapp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of yapp
 */
angular.module('yapp')
        .controller('UsuariosCtrl', function ($scope, $http, ngTableParams, $filter, ModalService) {

            $scope.usuarios = {};
            $http.get('api/usuarios')
                    .success(function (response) {
                        $scope.usuarios = response;
                    });

            $scope.param = new ngTableParams(
                    {
                        count: $scope.usuarios.length,
                        sorting: {nome: 'asc'}},
            {
                total: $scope.usuarios.length,
                counts: [],
                getData: function ($defer, params) {
                    $scope.usuarios = $filter('orderBy')($scope.usuarios, params.orderBy());
                    $defer.resolve($scope.usuarios);
                }
            });

            $scope.atualiza = function () {
                $http.get('api/usuarios')
                        .success(function (response) {
                            $scope.usuarios = response;
                            $scope.param.reload();
                        });
            };

            $scope.showModal = function (usuario) {
                ModalService.showModal({
                    templateUrl: "views/dashboard/modal/usuario.html",
                    controller: "UsuarioModalCtrl",
                    inputs: {
                        titulo: "Usuário",
                        usuario: usuario
                    }
                }).then(function (modal) {
                    modal.element.modal();
                    modal.close.then(function (result) {
                        if (result.atualiza) {
                            $scope.atualiza();
                        }
                    });
                });
            };
        });