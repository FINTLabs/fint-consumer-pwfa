pipeline {
    agent { label 'docker' }
    stages {
        stage('Build') {
            steps {
                sh 'git clean -fdx'
                sh "docker build -t ${GIT_COMMIT} ."
            }
        }
        stage('Publish Latest') {
            when {
                branch 'master'
            }
            steps {
                withDockerRegistry([credentialsId: 'fintlabs.azurecr.io', url: 'https://fintlabs.azurecr.io']) {
                    sh "docker tag ${GIT_COMMIT} fintlabs.azurecr.io/pwfa-consumer:${BUILD_NUMBER}"
                    sh "docker push 'fintlabs.azurecr.io/pwfa-consumer:${BUILD_NUMBER}'"
                }
            }
        }
    }
}
