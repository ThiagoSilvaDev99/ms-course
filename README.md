# HR Microservices Ecosystem ⚙️

Este repositório contém um **caso de estudo** prático desenvolvido durante um curso de especialização em Microsserviços com Spring Cloud.

> **💡 Objetivo do Projeto:** O foco principal desta implementação foi dominar a complexidade da **arquitetura distribuída** — como Service Discovery, API Gateway, Config Server e segurança centralizada (OAuth2/JWT). Por ser um laboratório focado em infraestrutura e comunicação em rede, o design interno das APIs (divisão estrita de camadas como Entity, Service e Controller) foi mantido de forma mais simples e direta ao ponto.

> **Nota:** A versão conteinerizada deste projeto (com imagens Docker e passo a passo de build) encontra-se na branch `docker`. Este README foca na execução e arquitetura do ambiente local via IDE.

---

## 🏛️ Arquitetura do Sistema

O projeto adota o padrão de microsserviços, onde cada domínio da aplicação é isolado, e a comunicação ocorre de forma dinâmica e segura.

A infraestrutura é composta por:
* **HR-Config-Server:** Servidor centralizado de configurações. Os microsserviços buscam suas credenciais e propriedades dinamicamente a partir deste servidor.
* **HR-Eureka-Server:** Service Discovery (A "Lista Telefônica"). Monitora quem está online e permite que os serviços se encontrem sem hardcode de portas ou IPs.
* **HR-API-Gateway-Zuul:** O ponto de entrada único da aplicação. Responsável por roteamento inteligente e por atuar como *Resource Server*, validando tokens JWT antes de liberar o acesso.
* **HR-OAuth:** Servidor de Autenticação. Valida credenciais e emite as "Pulseiras VIPs" (Tokens JWT) assinadas com chave secreta.

Os microsserviços de negócio são:
* **HR-User:** Gestão de usuários, perfis e papéis (Roles) com conexão direta ao banco de dados.
* **HR-Worker:** Gestão dos trabalhadores e seus dados diários.
* **HR-Payroll:** Serviço de folha de pagamento. Comunica-se com o `hr-worker` via **OpenFeign** para calcular os pagamentos de forma síncrona.

---

## 🛠️ Tecnologias Utilizadas

* **Java**
* **Spring Boot** (APIs RESTful)
* **Spring Cloud Netflix** (Eureka, Zuul, Ribbon, Hystrix)
* **Spring Cloud Config** (Gerenciamento centralizado)
* **Spring Security & OAuth2** (Autenticação e Autorização baseada em Roles)
* **JWT (JSON Web Token)** (Tokens seguros e assinados)
* **OpenFeign** (Comunicação HTTP declarativa entre microsserviços)
* **Maven** (Gerenciamento de dependências)

---

## 🚀 Como Executar Localmente

Como se trata de um sistema distribuído, a **ordem de inicialização é estritamente obrigatória** para que os serviços consigam registrar seus IPs e buscar suas configurações na rede.

### 1. Pré-requisitos
* Java instalado na máquina.
* IDE de sua preferência (IntelliJ IDEA, Eclipse, etc).
* Postman (ou similar) para testes de API.

### 2. Ordem de Inicialização (Boot)
Abra os projetos na sua IDE e execute as classes `main` estritamente nesta ordem:

1. **`hr-config-server`**: Aguarde iniciar na porta `8888`.
2. **`hr-eureka-server`**: Aguarde iniciar na porta `8761`. (Você pode acessar `http://localhost:8761` no navegador para ver o painel do Eureka).
3. **`hr-oauth`**, **`hr-user`**, **`hr-worker`**, **`hr-payroll`**: Podem ser iniciados simultaneamente. Eles buscarão as configs no passo 1 e se registrarão no passo 2.
4. **`hr-api-gateway-zuul`**: Inicie por último na porta `8765`. Ele mapeará as rotas de todos os serviços que já estão online.

---

## 🔐 Autenticação e Rotas (Postman)

Todas as requisições para os microsserviços (exceto o login) devem passar pelo Gateway (Zuul) e exigem um Token JWT no formato Bearer.

### 1. Gerando o Token (Login)
* **Endpoint:** `POST http://localhost:8765/hr-oauth/oauth/token`
* **Authorization:** Basic Auth (Client ID e Secret configurados no projeto).
* **Body (x-www-form-urlencoded):**
  * `grant_type`: `password`
  * `username`: `<email-do-usuario-no-banco>`
  * `password`: `<senha>`

### 2. Acessando Recursos
Copie o `access_token` devolvido na resposta anterior. Para acessar os serviços (ex: `hr-worker`), utilize:
* **Endpoint:** `GET http://localhost:8765/hr-worker/workers`
* **Headers:** `Authorization: Bearer <seu-token-aqui>`

*Dependendo do perfil (Role) atrelado ao usuário no banco, o Gateway (Resource Server) bloqueará ou permitirá o acesso à rota.*