pipeline {
    agent any

    tools {
        maven "maven 3.9.6"
    }
    stages {
        stage ("Git clone") {
            steps {
                git 'https://github.com/jackiekwofie/CarRentWebApp.git'
            }
        }
        stage ("Build with maven") {
            steps {
                sh "mvn clean test package"
            }
        }
    }
}