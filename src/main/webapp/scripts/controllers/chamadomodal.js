angular.module('yapp')
        .controller('ChamadoModalCtrl', ['$scope', '$http', '$element', 'titulo', 'chamado', 'close', 'md5', '$document', function ($scope, $http, $element, titulo, chamado, close, md5, $document) {
                $scope.titulo = titulo;
                if (chamado) {
                    $scope.assunto = chamado.assunto || '';
                    $scope.datahora = chamado.datahora || '';
                    $scope.descricao = chamado.descricao || '';
                    $scope.id = chamado.id || '';
                    $scope.tipoChamado = chamado.tipoChamado || '';
                    $scope.tipoFalha = chamado.tipoFalha || '';
                    $scope.tipoSituacao = chamado.tipoSituacao || '';
                    $scope.usuarioAtendimento = chamado.usuarioAtendimento || '';
                    $scope.usuarioAutor = chamado.usuarioAutor || '';
                } else {
                    $scope.assunto = '';
                    $scope.datahora = '';
                    $scope.descricao = '';
                    $scope.id = '';
                    $scope.tipoChamado = '';
                    $scope.tipoFalha = '';
                    $scope.tipoSituacao = '';
                    $scope.usuarioAtendimento = '';
                    $scope.usuarioAutor = '';
                }

                $scope.tipoFalhaLista = {};
                $http.get('api/tiposfalhas')
                        .success(function (response) {
                            $scope.tipoFalhaLista = response;
                        });

                $scope.save = function () {
                    if ($scope.form.$valid) {
                        var dados = {
                            id: $scope.id,
                            nome: $scope.nome,
                            email: $scope.email,
                            papel: $scope.papel,
                            senha: $scope.senha === '' ? '' : md5.createHash($scope.senha),
                            ativo: $scope.ativo || false
                        };
                        if (isNaN(parseInt($scope.id))) {
                            $http.post('api/usuarios', dados)
                                    .success(function () {
                                        $element.modal('hide');
                                        close({atualiza: true}, 500);
                                    })
                                    .error(function () {
                                        alert("Não foi possível salvar os dados.");
                                    });
                        } else {
                            $http.put('api/usuarios/' + $scope.id, dados)
                                    .success(function () {
                                        $element.modal('hide');
                                        close({atualiza: true}, 500);
                                    })
                                    .error(function () {
                                        alert("Não foi possível salvar os dados.");
                                    });
                        }
                    }
                };

                $scope.cancel = function () {
                    $element.modal('hide');
                    close({atualiza: false}, 500);
                };
            }]);