# Raptor Intelligence API (Ford Hub) 🦖

A **Raptor Intelligence API** é o backend oficial (escrito em Java e Spring Boot) que alimenta o aplicativo móvel **Raptor Intelligence**. Este sistema faz parte de uma solução de inteligência competitiva projetada para transformar a jornada de vendas dos consultores da Ford no Brasil.

## 🎯 Objetivo do Projeto

O objetivo principal desta API é fornecer dados técnicos precisos, estruturados e facilmente comparáveis de veículos (tanto da Ford, com destaque para a Ranger Raptor, quanto de seus concorrentes no mercado). A API gerencia a autenticação, o catálogo de veículos, as especificações detalhadas organizadas por categorias dinâmicas e o histórico de comparações feitas pelo consultor.

A arquitetura foi desenhada para ser escalável, modular e fortemente baseada em metadados (Spec Driven Development), permitindo a adição de novas categorias e veículos puramente via banco de dados, sem a necessidade de deploys de código adicionais.

## 🛠️ Stack Tecnológico

A API foi construída aderindo a padrões de Clean Architecture, utilizando as seguintes tecnologias:

- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.x
- **Banco de Dados:** PostgreSQL 16
- **Autenticação:** JWT (JSON Web Tokens) & Spring Security
- **Mapeamento de DTOs:** MapStruct
- **Validações:** Jakarta Validation
- **Boilerplate:** Lombok

## 🚀 Como Executar o Projeto

Para rodar este backend, você precisará do **Java 21**, **Maven** e **Docker** (para o banco de dados) instalados em sua máquina.

### Passo 1: Iniciar o Banco de Dados

A API depende de um banco de dados PostgreSQL preenchido. Felizmente, um ambiente Docker já está configurado na raiz do repositório principal.

1. Navegue até a **raiz do repositório** (um nível acima da pasta `ford-hub`):
   ```bash
   cd ..
   ```
2. Inicie os containers usando o Docker Compose. O arquivo `docker-compose.yml` irá subir o PostgreSQL e executar automaticamente o `schema.sql` para popular as tabelas:
   ```bash
   docker-compose up -d db
   ```
   *(Nota: Você pode rodar `docker-compose up -d` para subir tanto o banco de dados quanto a API via Docker, mas as instruções abaixo mostram como rodar a API localmente para desenvolvimento).*

### Passo 2: Executar a API Localmente

1. Retorne ou permaneça na pasta `ford-hub`:
   ```bash
   cd ford-hub
   ```
2. Execute o projeto usando o Maven Wrapper embutido:
   - **No Linux/Mac:**
     ```bash
     ./mvnw spring-boot:run
     ```
   - **No Windows:**
     ```cmd
     mvnw.cmd spring-boot:run
     ```

A aplicação Spring Boot iniciará e ficará disponível em `http://localhost:8080`.

---

## 📱 Executando em Conjunto com o `raptor-mobile`

Por padrão, o aplicativo móvel **Raptor Mobile** (feito em Expo/React Native) vem configurado para usar Mocks (dados estáticos) para que funcione offline.

Para fazer o aplicativo consumir esta API em tempo real, siga os passos abaixo:

### Passo 1: Suba a API
Certifique-se de que a API Java e o banco de dados PostgreSQL estão rodando conforme as instruções acima.

### Passo 2: Configure o Aplicativo Mobile
1. Vá para a pasta do aplicativo mobile:
   ```bash
   cd ../raptor-mobile
   ```
2. Crie ou edite o arquivo `.env` na raiz do `raptor-mobile`.
3. Defina as seguintes variáveis para desativar os Mocks e apontar para o seu backend. **Atenção:** Como você irá testar no celular ou emulador, o `localhost` pode não funcionar. **Use o endereço IP local da sua máquina na rede (ex: 192.168.1.15).**

   ```env
   EXPO_PUBLIC_USE_MOCKS=false
   EXPO_PUBLIC_API_URL=http://<SEU_ENDERECO_IP>:8080
   EXPO_PUBLIC_DEV_EMAIL=admin@ford.com.br
   EXPO_PUBLIC_DEV_PASSWORD=password
   ```

### Passo 3: Inicie o Mobile
Inicie o aplicativo limpando o cache para garantir que as novas variáveis de ambiente sejam carregadas:
```bash
npx expo start -c
```

Abra o aplicativo pelo app Expo Go. Ao fazer login usando `admin@ford.com.br` e `password`, as requisições serão enviadas para o backend Java na porta 8080.
