# Sistema de Microserviços - Desafio de Desenvolvimento

Este repositório contém o código de um sistema de microserviços desenvolvido em Java com Spring Boot, com foco na criação de um sistema de pedidos, estoque e gerenciamento de clientes. O sistema é estruturado como uma arquitetura de microserviços, com cada serviço responsável por uma área específica do sistema.

## Funcionalidades

### Microserviços

O sistema é composto por três microserviços principais:

1. **Microserviço de Pedidos (Order Service)**:
   - Criação de pedidos.
   - Consulta de pedidos.
   - Atualização do status do pedido.
   - Consulta de pedidos por cliente.

2. **Microserviço de Estoque (Stock Service)**:
   - Gerenciamento de produtos no estoque.
   - Criação, atualização, remoção de produtos.
   - Redução de quantidade de produtos.

3. **Microserviço de Clientes (Customer Service)**:
   - Criação e gerenciamento de clientes.
   - Consulta de clientes.
   - Consultar histórico de pedidos de um cliente.

### Tecnologias Utilizadas

- **Java 17**
- **Spring Boot**
- **Docker** (para a containerização dos microserviços)
- **PostgreSQL** (para armazenamento dos dados)
- **OpenFeign** (para comunicação entre microserviços)
- **Swagger** (para documentação da API)
- **JUnit e Mockito** (para testes unitários e de integração)

## Configuração do Ambiente

### Pré-requisitos

- Java 17 ou superior instalado.
- Docker instalado para rodar os microserviços e bancos de dados.
- Maven onfigurado para gerenciamento das dependências do projeto.

### Execução Local

Para rodar os microserviços localmente, você pode utilizar o Docker Compose, que irá configurar os containers necessários para rodar os serviços e o banco de dados PostgreSQL.

1. Clone o repositório:

    ```bash
    git clone https://github.com/usuário/repositório.git
    cd repositório
    ```

2. Compile os projetos e execute com o Docker:

    ```bash
    docker-compose up --build
    ```

### Estrutura de Diretórios

- `customer/`: Código do microserviço de gerenciamento de clientes.
- `order/`: Código do microserviço de gerenciamento de pedidos.
- `stock/`: Código do microserviço de gerenciamento de estoque.
- `docker-compose.yml`: Arquivo de configuração do Docker para rodar os microserviços e o banco de dados.


## Endpoints

### URLs Base das APIs

- **Order Service:** [http://localhost:8082/api/v1/orders](http://localhost:8082/api/v1/orders)
- **Customer Service:** [http://localhost:8081/api/v1/customers](http://localhost:8081/api/v1/customers)
- **Stock Service:** [http://localhost:8083/api/v1/stock](http://localhost:8083/api/v1/stock)


### Microserviço de Pedidos (Order Service)

- **POST** `/api/v1/orders/create`: Criação de um pedido.
- **GET** `/api/v1/orders/{id}`: Consultar um pedido por ID.
- **PUT** `/api/v1/orders/{id}/status`: Atualizar o status de um pedido.
- **GET** `/api/v1/orders/customer/{customerId}`: Consultar pedidos de um cliente específico.

### Microserviço de Estoque (Stock Service)

- **GET** `/api/v1/stock/product/{id}`: Consultar um produto por ID.
- **PUT** `/api/v1/stock/update`: Atualizar a quantidade de um produto.
- **POST** `/api/v1/stock/create`: Criar um novo produto no estoque.
- **PUT** `/api/v1/stock/{id}/reduce`: Reduzir a quantidade de um produto no estoque.
- **DELETE** `/api/v1/stock/{id}`: Excluir um produto do estoque.

### Microserviço de Clientes (Customer Service)

- **POST** `/api/v1/customers/create`: Criar um novo cliente.
- **GET** `/api/v1/customers/{id}`: Consultar um cliente por ID.
- **GET** `/api/v1/customers/{id}/orders`: Consultar os pedidos de um cliente específico.
- **DELETE** `/api/v1/customers/delete/{id}`: Excluir um cliente.

