'use strict';

angular.module('yapp')
        .controller('RelatoriosCtrl', function ($scope, $http) {

            $scope.relatorios = [];
            $scope.relatorioSelecionado = {};
            $http.get('api/relatorios')
                    .success(function (response) {
                        $scope.relatorios = response;
                        $scope.relatorioSelecionado = $scope.relatorios[0];
                    });

            function openSaveAsDialog(filename, content, mediaType) {
                var blob = new Blob([content], {type: mediaType});
                saveAs(blob, filename);
            }

            $scope.download = function () {
                var filename = 'relatorio.pdf';
                var url = 'api/relatorios/' + $scope.relatorioSelecionado.nome;
                var expectedMediaType = 'application/pdf';
                $http.get(url, {responseType: 'arraybuffer'})
                        .success(function (response) {
                            openSaveAsDialog(filename, response, expectedMediaType);
                        });
            };

        });
