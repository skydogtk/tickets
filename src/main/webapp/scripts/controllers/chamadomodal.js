angular.module('yapp')
        .controller('ChamadoModalCtrl', ['$scope', '$http', '$element', 'titulo', 'chamado', 'close', 'md5', '$document', '$filter', 'ngTableParams', '$rootScope', '$cookieStore', function ($scope, $http, $element, titulo, chamado, close, md5, $document, $filter, ngTableParams, $rootScope, $cookieStore) {

                $rootScope.globals = $cookieStore.get('globals') || {};
                $scope.papel = $rootScope.globals.currentUser.papel;

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

                    $scope.atualizaHistorico = function () {
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
                                        $scope.atualizaHistorico();
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
                            var dados = {
                                usuarioAtendimento: $scope.usuarioAtendimento
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

                $scope.showAtender = function () {
                    if ($scope.papel.id === 2 && $scope.tipoSituacao.id === 1) {
                        return true;
                    } else {
                        return false;
                    }
                };

                $scope.showEncerrar = function () {
                    if ($scope.papel.id === 2 && $scope.tipoSituacao.id === 2) {
                        return true;
                    } else {
                        return false;
                    }
                };

                $scope.showRetomar = function () {
                    if ($scope.papel.id === 1 && $scope.tipoSituacao.id === 3) {
                        return true;
                    } else {
                        return false;
                    }
                };

                $scope.atender = function () {
                    $http.put('api/chamados/atender/' + $scope.id)
                            .success(function (response) {
                                if (response.sucesso) {
                                    $scope.atualizaChamado();
                                    $scope.atualizaHistorico();
                                } else {
                                    alert(response.mensagem);
                                }
                            })
                            .error(function () {
                                alert("Não foi possível realizar a operação.");
                            });
                };

                $scope.encerrar = function () {
                    $http.put('api/chamados/encerrar/' + $scope.id)
                            .success(function (response) {
                                if (response.sucesso) {
                                    $scope.atualizaChamado();
                                    $scope.atualizaHistorico();
                                } else {
                                    alert(response.mensagem);
                                }
                            })
                            .error(function () {
                                alert("Não foi possível realizar a operação.");
                            });
                };

                $scope.retomar = function () {
                    $http.put('api/chamados/retomar/' + $scope.id)
                            .success(function (response) {
                                if (response.sucesso) {
                                    $scope.atualizaChamado();
                                    $scope.atualizaHistorico();
                                } else {
                                    alert(response.mensagem);
                                }
                            })
                            .error(function () {
                                alert("Não foi possível realizar a operação.");
                            });
                };

                $scope.showGravar = function () {
                    if (isNaN(parseInt($scope.id)) || ($scope.papel.id === 1 && $scope.tipoSituacao.id === 2)) {
                        return true;
                    } else {
                        return false;
                    }
                };

                $scope.enableAtendimento = function () {
                    if ($scope.papel.id === 1 && $scope.tipoSituacao.id === 2) {
                        return false;
                    } else {
                        return true;
                    }
                };

                $scope.cancel = function () {
                    $element.modal('hide');
                    close({atualiza: true}, 500);
                };

                $scope.atualizaChamado = function () {
                    $http.get('api/chamados/' + $scope.id)
                            .success(function (response) {
                                $scope.assunto = response.assunto || '';
                                $scope.datahora = $filter('date')(new Date(response.datahora), 'dd/MM/yyyy');
                                $scope.descricao = response.descricao || '';
                                $scope.id = response.id || '';
                                $scope.tipoChamado = response.tipoChamado || '';
                                $scope.tipoFalha = response.tipoFalha || '';
                                $scope.tipoSituacao = response.tipoSituacao || '';
                                $scope.usuarioAtendimento = response.usuarioAtendimento || '';
                                $scope.usuarioAutor = response.usuarioAutor || '';
                                $scope.autor = response.usuarioAutor.nome + " (" + response.usuarioAutor.papel.desc + ")";
                                $scope.mensagem = '';
                            });
                };
            }]);