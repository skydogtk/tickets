'use strict';

/**
 * @ngdoc function
 * @name yapp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of yapp
 */
angular.module('yapp')
        .controller('ChamadosCtrl', function ($scope, $http, ngTableParams, $filter, ModalService) {

            $scope.chamados = {};
            $http.get('api/chamados')
                    .success(function (response) {
                        $scope.chamados = response;
                    });

            $scope.param = new ngTableParams(
                    {
                        count: $scope.chamados.length,
                        sorting: {id: 'desc'}},
            {
                total: $scope.chamados.length,
                counts: [],
                getData: function ($defer, params) {
                    $scope.chamados = $filter('orderBy')($scope.chamados, params.orderBy());
                    $defer.resolve($scope.chamados);
                }
            });

            $scope.atualiza = function () {
                $http.get('api/chamados')
                        .success(function (response) {
                            $scope.chamados = response;
                            $scope.param.reload();
                        });
            };

            $scope.showModal = function (param) {
                var opt = {
                    templateUrl: "views/dashboard/modal/chamado.html",
                    controller: "ChamadoModalCtrl",
                    inputs: {
                        chamado: param.chamado
                    }
                };

                if (param.tipo) {
                    opt.inputs.titulo = param.tipo;
                } else {
                    opt.inputs.titulo = param.chamado.tipoChamado.desc;
                }

                ModalService.showModal(opt).then(function (modal) {
                    modal.element.modal();
                    modal.close.then(function (result) {
                        if (result.atualiza) {
                            $scope.atualiza();
                        }
                    });
                });
            };
        });