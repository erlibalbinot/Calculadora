# Calculadora de Empréstimos
Projeto para calculo de parcelas de empréstimos

---

## ⚙️ Pré-requisitos

- Java 21+
- Maven 3.9+
- node 20
- docker

---

## 🚀 Executando a aplicação no docker

1. Clone o repositório:

```bash
git clone https://github.com/erlibalbinot/Calculadora.git
cd Calculadora
```
2. Rode o build e suba os containers
```bash
docker compose build --no-cache
docker compose up
```
3. Acesse a api em
```bash
http://localhost:3000
```

---

## 🚀 Executando a aplicação localmente

1. Clone o repositório:

```bash
git clone https://github.com/erlibalbinot/Calculadora.git
```
2. Acesse a pasta frontend e rode a aplicação
```bash
cd Calculadora/frontend
npm start
```
3. Acesse a pasta backend e rode a aplicação
```bash
cd Calculadora/backend
mvn spring-boot:run
```
4. Acesse a api em
```bash
http://localhost:3000
```
