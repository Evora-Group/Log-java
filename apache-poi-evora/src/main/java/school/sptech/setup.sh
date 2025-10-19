#!/bin/bash

# =====================================
# ÉVORA PROJECT - SETUP AUTOMÁTICO
# =====================================

echo "Iniciando configuração do Évora Project..."
sleep 1

# -------------------------------------
# 1. Verifica se o Java 21 está instalado
# -------------------------------------
echo "Verificando instalação do Java 21..."
if type -p java >/dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo "Java encontrado (versão $JAVA_VERSION)"
    if [[ "$JAVA_VERSION" != 21* ]]; then
        echo "Atenção: versão detectada diferente da 21. Recomenda-se Java 21."
        read -p "Deseja instalar o Java 21 agora? (s/n): " instalar_java
        if [[ "$instalar_java" == "s" || "$instalar_java" == "S" ]]; then
            sudo apt update
            sudo apt install -y openjdk-21-jdk
        else
            echo "Continuando com a versão atual..."
        fi
    fi
else
    echo "Java não encontrado."
    read -p "Deseja instalar o Java 21 automaticamente? (s/n): " instalar_java
    if [[ "$instalar_java" == "s" || "$instalar_java" == "S" ]]; then
        sudo apt update
        sudo apt install -y openjdk-21-jdk
    else
        echo "Instale o Java 21 manualmente e execute novamente o script."
        exit 1
    fi
fi

# -------------------------------------
# 2. Verifica ou instala Maven
# -------------------------------------
echo "Verificando instalação do Maven..."
if type -p mvn >/dev/null; then
    echo "Maven encontrado em: $(which mvn)"
else
    echo "Maven não encontrado. Instalando..."
    sudo apt install -y maven
fi

# -------------------------------------
# 3. Determina a raiz do projeto
# -------------------------------------
# Sobe diretórios até encontrar o pom.xml
PROJECT_ROOT="$PWD"
while [ ! -f "$PROJECT_ROOT/pom.xml" ] && [ "$PROJECT_ROOT" != "/" ]; do
    PROJECT_ROOT=$(dirname "$PROJECT_ROOT")
done

if [ ! -f "$PROJECT_ROOT/pom.xml" ]; then
    echo "Erro: arquivo pom.xml não encontrado em nenhum diretório acima."
    echo "Execute este script dentro de uma pasta do projeto Maven."
    exit 1
fi

echo "Raiz do projeto detectada em: $PROJECT_ROOT"

# -------------------------------------
# 4. Cria o arquivo .env na raiz do projeto
# -------------------------------------
ENV_PATH="$PROJECT_ROOT/.env"

if [ -f "$ENV_PATH" ]; then
    echo "O arquivo .env já existe em: $ENV_PATH"
    read -p "Deseja sobrescrever? (s/n): " sobrescrever
    if [[ "$sobrescrever" != "s" && "$sobrescrever" != "S" ]]; then
        echo "Mantendo o arquivo existente."
    else
        rm "$ENV_PATH"
    fi
fi

if [ ! -f "$ENV_PATH" ]; then
    read -p "Digite o nome de usuário do banco de dados (DB_USER): " DB_USER
    read -s -p "Digite a senha do banco de dados (DB_PASS): " DB_PASS
    echo ""
    echo "DB_USER=$DB_USER" >> "$ENV_PATH"
    echo "DB_PASS=$DB_PASS" >> "$ENV_PATH"
    echo "Arquivo .env criado com sucesso em: $ENV_PATH"
fi

# -------------------------------------
# 5. Compila e executa o projeto Java
# -------------------------------------
read -p "Deseja compilar e executar o projeto agora? (s/n): " executar

if [[ "$executar" == "s" || "$executar" == "S" ]]; then
    cd "$PROJECT_ROOT"
    echo "Compilando o projeto..."
    mvn clean compile

    echo "Executando aplicação Java..."
    MAIN_CLASS="school.sptech.Main"  # Altere para a classe principal real
    mvn exec:java -Dexec.mainClass="$MAIN_CLASS"
else
    echo "Setup concluído. Para rodar o projeto manualmente use:"
    echo "cd \"$PROJECT_ROOT\" && mvn exec:java -Dexec.mainClass=\"school.sptech.Main\""
fi

echo "Configuração finalizada com sucesso."
