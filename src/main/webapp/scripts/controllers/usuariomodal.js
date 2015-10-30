angular.module('yapp')
        .controller('UsuarioModalCtrl', ['$scope', '$http', '$element', 'titulo', 'usuario', 'close', 'md5', '$document', function ($scope, $http, $element, titulo, usuario, close, md5, $document) {
                $scope.titulo = titulo;
                if (usuario) {
                    $scope.id = usuario.id || '';
                    $scope.nome = usuario.nome || '';
                    $scope.email = usuario.email || '';
                    $scope.senha = '';
                    $scope.papel = usuario.papel || '';
                    $scope.ativo = usuario.ativo || '';
                } else {
                    $scope.id = '';
                    $scope.nome = '';
                    $scope.email = '';
                    $scope.senha = '';
                    $scope.papel = '';
                    $scope.ativo = '';
                }

                $scope.papeis = {};
                $http.get('api/papeis')
                        .success(function (response) {
                            $scope.papeis = response;
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