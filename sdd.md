# Raptor Intelligence API — Spec Driven Development (SDD)

## Visão Geral

Este documento converte o arquivo original da **Raptor Intelligence API** para o formato Markdown utilizando a abordagem de **Spec Driven Development (SDD)**.

O objetivo deste documento é servir como **fonte única de verdade** para uma IA (Gemini CLI, Cursor, Copilot, Claude Code ou qualquer outro agente de desenvolvimento) implementar a API seguindo exatamente as regras arquiteturais, estruturais e funcionais definidas.

---

# Objetivo do Projeto

A **Raptor Intelligence API** é uma REST API construída em:

* Java 21
* Spring Boot 3.x
* PostgreSQL 16
* JWT Authentication
* Spring Security
* Spring Data JPA

Ela serve um aplicativo mobile (React Native / Expo) de inteligência competitiva para a Ford Ranger Raptor, permitindo que consultores Ford comparem especificações técnicas de veículos concorrentes com o modelo de referência.

---

# Regras Gerais de Desenvolvimento (SDD)

## Regras obrigatórias

A implementação deve seguir rigorosamente:

* Clean Architecture
* Separação por camadas
* DTO Pattern
* Spring Security Stateless
* Repository Pattern
* ResponseEntity em todos os controllers
* Tratamento global de exceções
* Nenhuma categoria hardcoded
* Comparações baseadas dinamicamente na tabela `spec_category_map`

---

# Stack Obrigatória

| Camada      | Tecnologia            |
| ----------- | --------------------- |
| Linguagem   | Java 21               |
| Framework   | Spring Boot 3.x       |
| ORM         | Spring Data JPA       |
| Banco       | PostgreSQL 16         |
| Segurança   | Spring Security + JWT |
| Build Tool  | Maven                 |
| DTO Mapper  | MapStruct             |
| Validação   | Jakarta Validation    |
| Boilerplate | Lombok                |

---

# 1. Visão Geral

## Funcionalidades principais

* Autenticação de usuário com JWT
* Ficha técnica completa por veículo
* Comparativo entre dois veículos com dados agrupados por categoria:

    * Motor
    * Off-road
    * Desempenho
    * Consumo
    * Tecnologia
    * Segurança
    * Conforto
    * Preço
* Score normalizado por categoria para exibição em radar chart no app
* Histórico de comparações favoritas por usuário autenticado

---

# 2. Banco de Dados

O banco PostgreSQL já existe e já está populado.

## Tabelas existentes

| Tabela            | Descrição                   |
| ----------------- | --------------------------- |
| brands            | Fabricantes dos veículos    |
| vehicles          | Modelos e versões avaliados |
| engine_specs      | Specs de motorização        |
| drivetrain_specs  | Transmissão e tração        |
| suspension_specs  | Suspensão e off-road        |
| dimensions        | Dimensões e capacidades     |
| warranty          | Garantia e pós-venda        |
| features          | Catálogo de itens           |
| vehicle_features  | Relação N:M                 |
| spec_category_map | Mapeamento de specs         |
| app_users         | Usuários autenticados       |
| comparisons       | Comparações salvas          |

---

# Regra Crítica — spec_category_map

A tabela `spec_category_map` é o núcleo da lógica da aplicação.

## IMPORTANTE

A API deve montar o comparativo dinamicamente utilizando esta tabela.

### PROIBIDO

* Hardcode de categorias
* Hardcode de campos
* Hardcode de labels
* Hardcode de radar score

Toda a estrutura do comparativo deve vir da tabela `spec_category_map`.

---

# 3. Estrutura do Projeto

## Criação via Spring Initializr

Configuração obrigatória:

| Campo       | Valor                   |
| ----------- | ----------------------- |
| Project     | Maven                   |
| Language    | Java                    |
| Spring Boot | 3.x                     |
| Group       | com.ford                |
| Artifact    | raptor-intelligence-api |
| Java        | 21                      |
| Packaging   | Jar                     |

---

# Dependências obrigatórias

## Spring Initializr

Selecionar:

* Spring Web
* Spring Data JPA
* Spring Security
* PostgreSQL Driver
* Lombok
* Validation

---

# Dependências adicionais (pom.xml)

## JWT

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

---

## MapStruct

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

---

# 4. Estrutura de Pacotes

Criar manualmente:

```text
src/main/java/com/ford/raptorapi/

config/
controller/
dto/
  request/
  response/
exception/
model/
  enums/
repository/
service/
security/
```

---

# 5. Configuração

## application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/raptor_intelligence
    username: raptor
    password: raptor123
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate

    show-sql: false

    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

server:
  port: 8080

jwt:
  secret: sua-chave-secreta-256bits-aqui-trocar-em-producao
  expiration: 86400000
```

---

# Regra Crítica — ddl-auto

## OBRIGATÓRIO

```yaml
ddl-auto: validate
```

## PROIBIDO

```yaml
ddl-auto: create
ddl-auto: update
```

---

# 6. Entidades JPA

## Padrões obrigatórios

| Anotação                     | Uso                 |
| ---------------------------- | ------------------- |
| @Entity                      | Toda entidade       |
| @Table(name="")              | Nome da tabela      |
| @Id                          | PK                  |
| @GeneratedValue(IDENTITY)    | IDs serial          |
| @Column(name="")             | Divergência Java/BD |
| @Enumerated(EnumType.STRING) | ENUMs               |
| @ManyToOne(fetch = LAZY)     | Relacionamentos     |
| @OneToOne(fetch = LAZY)      | Relacionamentos     |
| @Data                        | Lombok              |
| @NoArgsConstructor           | Lombok              |

---

# 6.1 Enums

## FuelType.java

```java
public enum FuelType {
    gasolina,
    diesel,
    eletrico,
    hibrido,
    phev
}
```

---

## VehicleCategory.java

```java
public enum VehicleCategory {
    desert_runner,
    rock_crawler,
    diesel_global,
    full_size,
    hybrid_disruptor
}
```

---

# 6.2 Vehicle.java

## Campos obrigatórios

| Campo           | Tipo            |
| --------------- | --------------- |
| id              | Integer         |
| brand           | Brand           |
| model           | String          |
| version         | String          |
| modelYear       | Short           |
| fuelType        | FuelType        |
| category        | VehicleCategory |
| isReference     | Boolean         |
| imageUrl        | String          |
| engineSpecs     | EngineSpecs     |
| drivetrainSpecs | DrivetrainSpecs |
| suspensionSpecs | SuspensionSpecs |
| dimensions      | Dimensions      |
| warranty        | Warranty        |

---

# 6.3 Outras Entidades

## Implementar

* Brand
* EngineSpecs
* DrivetrainSpecs
* SuspensionSpecs
* Dimensions
* Warranty
* Feature
* VehicleFeature
* SpecCategoryMap
* AppUser
* Comparison

---

# VehicleFeature

## Regra obrigatória

Utilizar:

```java
@EmbeddedId
```

---

# AppUser

## Regra obrigatória

A entidade deve implementar:

```java
UserDetails
```

---

# 6.4 SQL obrigatório

Executar antes da API subir:

```sql
CREATE TABLE app_users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    dealership VARCHAR(150),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

# 7. Repositories

Todos devem extender:

```java
JpaRepository<Entidade, TipoId>
```

---

# Métodos custom obrigatórios

## VehicleRepository

```java
findByCategory(VehicleCategory)
findByFuelType(FuelType)
findByIsReferenceTrue()
```

---

## SpecCategoryMapRepository

```java
findByCategoryKeyIn(List<String>)
findByCategoryKeyOrderByDisplayOrder(String)
```

---

## AppUserRepository

```java
findByEmail(String)
```

---

## ComparisonRepository

```java
findByUserId(Integer)
```

---

# 8. DTOs

## REGRA CRÍTICA

### NUNCA retornar entidades JPA diretamente

Sempre utilizar DTOs.

---

# DTOs de Request

## AuthRequest

```java
String email
String password
```

### Validações

```java
@NotBlank
```

---

## SaveComparisonRequest

```java
Integer vehicleAId
Integer vehicleBId
String notes
```

### Validações

```java
@NotNull
```

---

# DTOs de Response

## AuthResponse

```java
token
name
dealership
```

---

## VehicleSummaryResponse

```java
id
brand
model
version
modelYear
fuelType
category
isReference
imageUrl
```

---

## VehicleDetailResponse

Todos os campos do summary + specs relacionadas.

---

## CompareResponse

```java
vehicles: List<VehicleSummaryResponse>
categories: Map<String, CategoryCompareData>
```

---

## CategoryCompareData

```java
label
radarScore
specs
```

---

## SpecRow

```java
label
unit
values
```

---

# Estrutura JSON obrigatória — CompareResponse

```json
{
  "vehicles": [
    {
      "id": 1,
      "brand": "Ford",
      "model": "Ranger"
    }
  ],
  "categories": {
    "motor": {
      "label": "Motor",
      "radarScore": {
        "1": 85.0
      },
      "specs": [
        {
          "label": "Potencia",
          "unit": "cv",
          "values": {
            "1": "405"
          }
        }
      ]
    }
  }
}
```

---

# 9. Segurança JWT

## Classes obrigatórias

| Classe         | Responsabilidade                |
| -------------- | ------------------------------- |
| JwtService     | Gerar e validar token           |
| JwtAuthFilter  | Interceptar requests            |
| SecurityConfig | Configuração do Spring Security |
| AppUser        | UserDetails                     |

---

# Rotas públicas

| Rota         | Método |
| ------------ | ------ |
| /auth/**     | POST   |
| /vehicles/** | GET    |
| /brands/**   | GET    |
| /compare     | GET    |

---

# Rotas protegidas

| Rota            |
| --------------- |
| /comparisons/** |
| qualquer outra  |

---

# Fluxo de autenticação

1. POST `/auth/login`
2. Validação via BCrypt
3. Geração do JWT
4. Retorno do token
5. App armazena token
6. Header Authorization Bearer
7. JwtAuthFilter valida token
8. SecurityContext é populado

---

# 10. Services

## AuthService

```java
login(AuthRequest)
```

---

## VehicleService

```java
listAll()
findById(id)
findByCategory(cat)
findByFuelType(fuel)
```

---

## CompareService

```java
compare(List<Integer> ids, List<String> categories)
```

---

## ComparisonService

```java
findByUser(userId)
save(userId, request)
delete(id, userId)
```

---

# 10.1 CompareService — Regra MAIS IMPORTANTE do projeto

## Fluxo obrigatório

### 1. Buscar veículos

```java
VehicleRepository.findAllById()
```

---

### 2. Buscar mappings

```java
SpecCategoryMapRepository.findByCategoryKeyIn()
```

---

### 3. Agrupar por category_key

Utilizar:

```java
Collectors.groupingBy()
```

---

### 4. Reflection

Usar:

```java
getDeclaredField()
field.get()
```

---

### 5. vehicle_features

Buscar specs na relação N:M.

---

# 10.2 Radar Score

## Numéricos

```text
(valorVeiculo / maiorValor) * 100 * radar_weight
```

---

## Booleanos

```text
true = 100
false = 0
```

---

## Texto

```text
não nulo = 100
```

---

# Métricas invertidas

Para métricas onde menor é melhor:

```text
(min_valor / valor_veiculo) * 100 * radar_weight
```

---

# 11. Controllers

| Controller           | Base Path    |
| -------------------- | ------------ |
| AuthController       | /auth        |
| VehicleController    | /vehicles    |
| CompareController    | /compare     |
| ComparisonController | /comparisons |
| BrandController      | /brands      |

---

# Endpoints obrigatórios

| Método | Endpoint                      |
| ------ | ----------------------------- |
| POST   | /auth/login                   |
| GET    | /vehicles                     |
| GET    | /vehicles/{id}                |
| GET    | /vehicles/category/{category} |
| GET    | /brands                       |
| GET    | /compare                      |
| GET    | /comparisons                  |
| POST   | /comparisons                  |
| DELETE | /comparisons/{id}             |

---

# Padrões HTTP

| Status | Uso          |
| ------ | ------------ |
| 200    | Sucesso      |
| 201    | Criação      |
| 204    | Delete       |
| 400    | Validação    |
| 401    | Unauthorized |
| 404    | Not Found    |

---

# 12. Exception Handling

## Classes obrigatórias

| Classe                    |
| ------------------------- |
| GlobalExceptionHandler    |
| ResourceNotFoundException |
| UnauthorizedException     |
| ErrorResponse             |

---

# Exceções obrigatórias

| Exceção                         | HTTP |
| ------------------------------- | ---- |
| ResourceNotFoundException       | 404  |
| BadCredentialsException         | 401  |
| MethodArgumentNotValidException | 400  |
| AccessDeniedException           | 403  |
| Exception                       | 500  |

---

# ErrorResponse

```java
status
message
timestamp
```

---

# 13. CORS

## CorsConfig.java

```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "DELETE",
                "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
```

---

# 14. Ordem de Implementação (SDD)

## Etapa 1

Criar projeto + application.yml

### Validação

Aplicação sobe na porta 8080.

---

## Etapa 2

Criar enums + entidades.

### Validação

`ddl-auto: validate` passa sem erro.

---

## Etapa 3

Criar repositories.

### Validação

Compilação sem erros.

---

## Etapa 4

Criar JWT + Security.

### Validação

Compilação sem erros.

---

## Etapa 5

Criar Auth.

### Validação

POST `/auth/login` retorna JWT.

---

## Etapa 6

Criar Vehicles.

### Validação

GET `/vehicles` retorna dados.

---

## Etapa 7

Criar CompareService.

### Validação

GET `/compare` retorna JSON correto.

---

## Etapa 8

Criar Comparisons.

### Validação

Endpoints autenticados funcionando.

---

## Etapa 9

Criar Exception Handler.

### Validação

JSON padronizado de erro.

---

## Etapa 10

Configurar CORS.

### Validação

App mobile acessa API sem bloqueio.



# Regras Finais para IA

## OBRIGATÓRIO

* Seguir exatamente os nomes do banco
* Não alterar tabelas existentes
* Não hardcodar categorias
* Não retornar entidades JPA
* Usar DTOs
* Usar JWT Stateless
* Usar fetch LAZY
* Implementar CompareService dinamicamente

---

# Objetivo Arquitetural Final

A API deve ser:

* Escalável
* Modular
* Extensível
* Baseada em metadados
* Compatível com novas categorias sem alteração de código
* Totalmente orientada à configuração do banco

---

# Resultado Esperado

Ao finalizar a implementação, a API deverá:

* Autenticar usuários
* Retornar fichas técnicas
* Comparar veículos dinamicamente
* Calcular radar score
* Salvar comparações
* Funcionar integrada ao app React Native/Expo
* Operar totalmente via PostgreSQL + Spring Boot + JWT
