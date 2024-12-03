# Fase 1: Construir a aplicação
FROM maven:3.8.4-openjdk-17-slim AS build

# Diretório de trabalho para a fase de build
WORKDIR /app

# Copiar o arquivo pom.xml e baixar as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar o código fonte
COPY src /app/src

# Compilar o projeto e gerar o .jar
RUN mvn clean package -DskipTests

# Fase 2: Rodar a aplicação
FROM openjdk:17-jdk-slim

# Diretório de trabalho para a fase de execução
WORKDIR /app

# Copiar o .jar gerado na fase anterior
COPY --from=build /app/target/order-0.0.1-SNAPSHOT.jar app.jar

# Expor a porta que a aplicação vai rodar
EXPOSE 8082

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
