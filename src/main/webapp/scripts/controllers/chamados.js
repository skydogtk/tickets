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
                        sorting: {id: 'asc'}},
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

            $scope.showModal = function (chamado) {
                var opt = {};

                if (chamado) {
                    opt = {
                        templateUrl: "views/dashboard/modal/solicitacao.html",
                        controller: "SolicitacaoModalCtrl",
                        inputs: {
                            titulo: "Solicitação",
                            chamado: chamado
                        }
                    };
                } else {
                    opt = {
                        templateUrl: "views/dashboard/modal/incidente.html",
                        controller: "IncidenteModalCtrl",
                        inputs: {
                            titulo: "Incidente",
                            chamado: chamado
                        }
                    };
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