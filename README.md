# 🏥 Sistema de Gerenciamento Hospitalar

Um sistema completo para gerenciamento de clínicas e hospitais, desenvolvido em **Java**, utilizando **Spring Boot**, **MySQL** e páginas dinâmicas com **Thymeleaf**.  
Permite organizar médicos, pacientes, consultas, exames e procedimentos de forma simples e acessível via navegador.

---

## 🚀 Tecnologias Utilizadas

- **Java 21+**
- **Spring Boot**
- **Spring Data JPA**
- **MySQL**
- **Thymeleaf**
- **Bootstrap 5**
- **Maven**

---

## 📂 Estrutura do Projeto

Segue a arquitetura **MVC**:

- **Model** → Entidades do sistema (Paciente, Médico, Consulta, etc.)  
- **Repository** → Interfaces JPA para acesso ao banco  
- **Controller** → Rotas e retorno de páginas Web  
- **View** → Templates Thymeleaf (`/templates`)

---

## 🗄️ Banco de Dados (MySQL)

O sistema depende de um servidor **MySQL ativo** e configurado corretamente.

No arquivo:

⚠️ **Importante:**  
- O MySQL deve estar **rodando antes** de iniciar o sistema.  
- Utilize a mesma senha configurada no projeto, que é root.  
- Caso queira outra senha, atualize no `application.properties`.
```bash
spring.datasource.username=root
spring.datasource.password=SuaSenha
```
- Recomenda-se o uso do **MySQL Workbench** para gerenciar o banco de dados.

---

## ▶️ Como Executar o Sistema

### 1️⃣ Pré-requisitos
- JDK **21+**
- Maven
- MySQL funcionando localmente

### 2️⃣ Clonar o repositório

```bash
git clone https://github.com/gustavo-ferreirasantos/Sistema_de_Gerenciamento_Hospitalar

cd Sistema_de_Gerenciamento_Hospitalar

idea . (Usando IntelliJ IDEA)
```
- Atualize as dependências através do arquivo pom.xml, clicando em “Load Maven Project”
- Execute o arquivo StartApplication
- Após a execução do programa, acesse qualquer navegador no endereço: http://localhost:8080/
