pipeline {
    agent any

    environment {
        COMPOSE_PROJECT_NAME = 'carla-config-hub'
    }

    stages {
        // 1. Scarica il codice aggiornato da GitHub
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // 2. Ferma eventuali container vecchi rimasti accesi
        stage('Clean Old Deploy') {
            steps {
                script {
                    echo '🛑 Fermando vecchi container...'
                    // "|| true" serve a non far fallire la build se non ci sono container attivi
                    sh 'docker-compose down || true'
                }
            }
        }

        // 3. Costruisce le immagini usando i Dockerfile
        stage('Build Services') {
            steps {
                script {
                    echo '🔨 Costruendo le immagini Docker...'
                    sh 'docker-compose build'
                }
            }
        }

        // 4. Avvia tutto in background
        stage('Deploy') {
            steps {
                script {
                    echo '🚀 Avvio dei servizi...'
                    sh 'docker-compose up -d'
                }
            }
        }

        // 5. Verifica che il Gateway risponda
        stage('Health Check') {
            steps {
                script {
                    echo '✅ Verifica stato container...'
                    sleep 15
                    sh 'docker ps'
                    sh 'docker logs carla-gateway-service --tail 20'
                }
            }
        }
    }
}