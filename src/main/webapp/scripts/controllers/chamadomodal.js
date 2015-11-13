angular.module('yapp')
        .controller('ChamadoModalCtrl', ['$scope', '$http', '$element', 'titulo', 'chamado', 'close', 'md5', '$document', '$filter', 'ngTableParams', function ($scope, $http, $element, titulo, chamado, close, md5, $document, $filter, ngTableParams) {
                $scope.titulo = titulo;
                if (chamado) {
                    $scope.assunto = chamado.assunto || '';
                    $scope.datahora = $filter('date')(new Date(chamado.datahora), 'dd/MM/yyyy');
                    $scope.descricao = chamado.descricao || '';
                    $scope.id = chamado.id || '';
                    $scope.tipoChamado = chamado.tipoChamado || '';
                    $scope.tipoFalha = chamado.tipoFalha || '';
                    $scope.tipoSituacao = chamado.tipoSituacao || '';
                    $scope.usuarioAtendimento = chamado.usuarioAtendimento || '';
                    $scope.usuarioAutor = chamado.usuarioAutor || '';
                    $scope.autor = chamado.usuarioAutor.nome + " (" + chamado.usuarioAutor.papel.desc + ")";
                    $scope.mensagem = '';

                    $scope.chamadoHistoricoLista = {};
                    $http.get('api/chamadohistorico/' + $scope.id)
                            .success(function (response) {
                                $scope.chamadoHistoricoLista = response;
                            });

                    $scope.param = new ngTableParams(
                            {
                                count: $scope.chamadoHistoricoLista.length,
                                sorting: {datahora: 'desc'}},
                    {
                        total: $scope.chamadoHistoricoLista.length,
                        counts: [],
                        getData: function ($defer, params) {
                            $scope.chamadoHistoricoLista = $filter('orderBy')($scope.chamadoHistoricoLista, params.orderBy());
                            $defer.resolve($scope.chamadoHistoricoLista);
                        }
                    });

                    $scope.atualiza = function () {
                        $http.get('api/chamadohistorico/' + $scope.id)
                                .success(function (response) {
                                    $scope.chamadoHistoricoLista = response;
                                    $scope.param.reload();
                                });
                    };

                    $scope.envia = function () {
                        if ($scope.mensagem !== '') {
                            $http.post('api/chamadohistorico/' + $scope.id, {mensagem: $scope.mensagem})
                                    .success(function () {
                                        $scope.mensagem = '';
                                        $scope.atualiza();
                                    })
                                    .error(function () {
                                        alert("Não foi possível salvar os dados.");
                                    });
                        }
                    };

                } else {
                    $scope.assunto = '';
                    $scope.datahora = '';
                    $scope.descricao = '';
                    $scope.id = '';
                    $scope.tipoChamado = $scope.titulo;
                    $scope.tipoFalha = '';
                    $scope.tipoSituacao = '';
                    $scope.usuarioAtendimento = '';
                    $scope.usuarioAutor = '';
                }

                $scope.usuarioAtendenteLista = {};
                $http.get('api/usuarios/atendentes')
                        .success(function (response) {
                            $scope.usuarioAtendenteLista = response;
                        });

                $scope.tipoFalhaLista = {};
                $http.get('api/tiposfalhas')
                        .success(function (response) {
                            $scope.tipoFalhaLista = response;
                        });

                $scope.tipoSituacaoLista = {};
                $http.get('api/tipossituacoes')
                        .success(function (response) {
                            $scope.tipoSituacaoLista = response;
                        });

                $scope.valida = function () {
                    if (
                            !$scope.assunto ||
                            !$scope.descricao) {
                        return false;
                    } else if (
                            $scope.titulo === 'Incidente' &&
                            isNaN(parseInt($scope.id)) &&
                            isNaN(parseInt($scope.tipoFalha.id))) {
                        return false;
                    } else {
                        return true;
                    }
                };

                $scope.save = function () {
                    if ($scope.valida()) {

                        if (isNaN(parseInt($scope.id))) {
                            console.log("POST");

                            var dados = {
                                assunto: $scope.assunto,
                                descricao: $scope.descricao,
                                tipoChamado: $scope.tipoChamado,
                                tipoFalha: $scope.tipoFalha
                            };
                            $http.post('api/chamados', dados)
                                    .success(function () {
                                        $element.modal('hide');
                                        close({atualiza: true}, 500);
                                    })
                                    .error(function () {
                                        alert("Não foi possível salvar os dados.");
                                    });
                        } else {
                            console.log("PUT");
                            var dados = {
                                assunto: $scope.assunto,
                                descricao: $scope.descricao,
                                tipoChamado: $scope.tipoChamado,
                                tipoFalha: $scope.tipoFalha
                            };
                            $http.put('api/chamados/' + $scope.id, dados)
                                    .success(function () {
                                        $element.modal('hide');
                                        close({atualiza: true}, 500);
                                    })
                                    .error(function () {
                                        alert("Não foi possível salvar os dados.");
                                    });
                        }
                    } else {
                        alert("Verifique campos obrigatórios");
                    }
                };
                $scope.cancel = function () {
                    $element.modal('hide');
                    close({atualiza: false}, 500);
                };
            }]);