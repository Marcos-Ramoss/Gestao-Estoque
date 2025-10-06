# Sistema de Controle de Estoque - Backend Java Spring Boot

Sistema de controle de estoque desenvolvido em Java com Spring Boot, seguindo princípios SOLID e arquitetura em camadas.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security + JWT**
- **Spring Data JPA + Hibernate**
- **MySQL 8.0**
- **Flyway** (Migração de banco)
- **OpenAPI/Swagger** (Documentação)
- **Lombok**
- **Docker Compose**

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Docker e Docker Compose
- MySQL 8.0 (ou usar Docker Compose)

## 🛠️ Configuração e Execução

### 1. Clone o repositório
```bash
git clone <repository-url>
cd controle-estoque-backend
```

### 2. Configure o banco de dados
```bash
# Inicie o MySQL com Docker Compose
docker-compose up -d mysql
```

### 3. Configure as variáveis de ambiente (opcional)
Crie um arquivo `.env` na raiz do projeto:
```env
DATABASE_URL=jdbc:mysql://localhost:3306/controle_estoque
DATABASE_USERNAME=app_user
DATABASE_PASSWORD=app_password
JWT_SECRET=sua-chave-secreta-super-segura-com-pelo-menos-32-caracteres-para-jwt
```

### 4. Execute a aplicação
```bash
# Compile e execute
mvn spring-boot:run

# Ou compile e execute o JAR
mvn clean package
java -jar target/controle-estoque-1.0.0.jar
```

### 5. Acesse a aplicação
- **API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **Health Check**: http://localhost:8080/api/actuator/health

## 🔐 Autenticação

### Usuário padrão criado automaticamente:
- **Email**: admin@controleestoque.com
- **Senha**: admin123
- **Role**: ADMIN

### Como fazer login:
1. Acesse `POST /api/auth/login`
2. Envie as credenciais:
```json
{
  "email": "admin@controleestoque.com",
  "senha": "admin123"
}
```
3. Use o token retornado no header `Authorization: Bearer <token>`

## 📚 Documentação da API

A documentação completa da API está disponível via Swagger UI em:
http://localhost:8080/api/swagger-ui.html

### Principais endpoints:

#### Autenticação
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout

#### Usuários (apenas ADMIN)
- `GET /api/usuarios` - Listar usuários
- `POST /api/usuarios` - Criar usuário
- `PUT /api/usuarios/{id}` - Atualizar usuário
- `DELETE /api/usuarios/{id}` - Excluir usuário

#### Categorias
- `GET /api/categorias` - Listar categorias
- `POST /api/categorias` - Criar categoria (ADMIN)
- `PUT /api/categorias/{id}` - Atualizar categoria (ADMIN)

#### Produtos
- `GET /api/produtos` - Listar produtos
- `POST /api/produtos` - Criar produto (ADMIN/VENDEDOR)
- `GET /api/produtos/estoque-baixo` - Produtos com estoque baixo
- `GET /api/produtos/sku/{sku}` - Buscar por SKU

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas bem definida:

```
src/main/java/com/controleestoque/
├── ControleEstoqueApplication.java          # Classe principal
├── config/                                  # Configurações
│   ├── SecurityConfig.java                 # Spring Security
│   ├── OpenApiConfig.java                  # Swagger/OpenAPI
├── controller/                             # Controllers REST
│   ├── AuthController.java                 # Autenticação
│   ├── UsuarioController.java              # Usuários
│   ├── ProdutoController.java              # Produtos
│   └── CategoriaController.java            # Categorias
├── service/                                # Lógica de negócio
│   ├── AuthService.java                    # Serviço de autenticação
│   ├── UsuarioService.java                 # Serviço de usuários
│   ├── ProdutoService.java                 # Serviço de produtos
│   └── CategoriaService.java               # Serviço de categorias
├── repository/                             # Acesso a dados
│   ├── UsuarioRepository.java              # Repository usuários
│   ├── ProdutoRepository.java              # Repository produtos
│   └── CategoriaRepository.java            # Repository categorias
├── entity/                                 # Entidades JPA
│   ├── Usuario.java                        # Entidade usuário
│   ├── Produto.java                        # Entidade produto
│   └── Categoria.java                      # Entidade categoria
├── dto/                                    # Data Transfer Objects
│   ├── request/                            # DTOs de entrada
│   ├── response/                           # DTOs de saída
│   └── mapper/                             # Mappers
├── security/                               # Segurança
│   ├── JwtAuthenticationFilter.java        # Filtro JWT
│   ├── JwtTokenProvider.java               # Provedor JWT
│   └── UserPrincipal.java                  # User Principal
├── exception/                              # Exceções globais
│   ├── GlobalExceptionHandler.java         # Handler global
│   ├── ResourceNotFoundException.java      # Recurso não encontrado
│   └── BusinessException.java              # Regra de negócio
└── enums/                                  # Enums
    ├── Role.java                           # Papéis do usuário
    └── TipoMovimentacao.java               # Tipos de movimentação
```

## 🔒 Segurança

- **JWT Authentication**: Tokens JWT para autenticação
- **Role-based Access Control**: ADMIN, VENDEDOR, VISUALIZADOR
- **BCrypt**: Criptografia de senhas
- **CORS**: Configurado para frontend
- **Validation**: Validação de entrada com Bean Validation

## 📊 Banco de Dados

### Tabelas principais:
- `usuarios` - Usuários do sistema
- `categorias` - Categorias de produtos
- `produtos` - Produtos e estoque
- `movimentacoes_estoque` - Histórico de movimentações
- `vendas` - Vendas e comissões

### Migrações:
As migrações são executadas automaticamente via Flyway na inicialização da aplicação.

## 🧪 Testes

```bash
# Executar todos os testes
mvn test

# Executar testes de integração
mvn verify
```

## 🚀 Deploy

### Docker
```bash
# Build da aplicação
mvn clean package

# Build da imagem Docker
docker build -t controle-estoque .

# Executar com Docker Compose
docker-compose up -d
```

### Produção
1. Configure as variáveis de ambiente para produção
2. Execute `mvn clean package`
3. Execute o JAR com as configurações de produção:
```bash
java -jar -Dspring.profiles.active=prod target/controle-estoque-1.0.0.jar
```

## 📝 Funcionalidades Implementadas

### ✅ Módulo de Usuários e Segurança
- [x] Cadastro de usuários com diferentes níveis de acesso
- [x] Login/logout seguro com JWT
- [x] Controle de acesso baseado em roles
- [x] CRUD completo de usuários

### ✅ Módulo de Produtos e Estoque
- [x] Cadastro completo de produtos
- [x] Consulta, edição e remoção de produtos
- [x] Controle de estoque em tempo real
- [x] Alertas de estoque baixo
- [x] Relacionamento com categorias

### ✅ Módulo de Categorias
- [x] CRUD completo de categorias
- [x] Status de ativação/desativação
- [x] Relacionamento com produtos

### 🔄 Módulo de Integração com Mercado Livre
- [ ] Integração com API do Mercado Livre
- [ ] Importação automática de vendas
- [ ] Atualização automática de estoque
- [ ] Cálculo de comissões
- [ ] Relatórios de comissões

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👨‍💻 Autor

**Marcos Oliveira**
- Email: marcos@controleestoque.com

---

Para mais informações, consulte a documentação da API via Swagger UI ou entre em contato.


