pipeline {
    agent any

    environment {
        COMPOSE_PROJECT_NAME = 'carla-config-hub'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean Old Deploy') {
            steps {
                script {
                    echo 'Fermando vecchi container...'
                    //
                    sh 'docker-compose down || true'
                }
            }
        }


        stage('Build Services') {
            steps {
                script {
                    echo 'Costruendo le immagini Docker...'
                    sh 'docker-compose build'
                }
            }
        }


        stage('Deploy') {
            steps {
                script {
                    echo '🚀 Avvio dei servizi...'
                    sh 'docker-compose up -d'
                }
            }
        }


        stage('Health Check') {
            steps {
                script {
                    echo 'Verifica stato container...'
                    sleep 15
                    sh 'docker ps'
                    sh 'docker logs carla-gateway-service --tail 20'
                }
            }
        }
    }
}