pipeline {
    agent any

    environment {
        registryCredential = 'docker-hub' // Docker Hub에 로그인할 때 사용할 자격 증명 ID
        dockerImage = '' // Docker 이미지 변수 초기화
    }

    stages {
        stage('Setup Credentials') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'docker-hub-username', variable: 'DOCKER_HUB_USERNAME'),
                                     string(credentialsId: 'bastion-username', variable: 'BASTION_USERNAME'),
                                     string(credentialsId: 'bastion-ip', variable: 'BASTION_IP')]) {
                        // 환경 변수 설정
                        env.dockerHubUsername = DOCKER_HUB_USERNAME
                        env.imageName = "popolog-apigateway-service"
                        env.bastionUsername = BASTION_USERNAME
                        env.bastionIp = BASTION_IP
                        env.fullImageName = "${env.dockerHubUsername}/${env.imageName}" // fullImageName 설정
                    }
                }
            }
        }

        stage('Cloning Repository') {
            steps {
                echo 'Cloning Repository'
                git url: 'https://github.com/KEA-5th-Myaong/apigateway-service.git',
                    branch: 'develop',
                    credentialsId: 'github-token'
            }
        }

        stage('Build Gradle') {
            steps {
                echo 'Building with Gradle'
                dir('.') {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                echo 'Building and Pushing Docker Image'
                script {
                    def previousBuildId = "${env.BUILD_ID.toInteger() - 1}"
                    def newBuildId = "${env.BUILD_ID.toInteger()}"

                   // 1. 로컬에 존재하는 latest 태그가 붙은 도커 이미지의 태그를 previousBuildId로 변경
                   sh "docker tag ${env.fullImageName}:latest ${env.fullImageName}:${previousBuildId} || true"

                   // 2. 원격 도커 허브에서 latest 태그의 이미지 삭제
                   docker.withRegistry('', registryCredential) {
                       sh "docker rmi ${env.fullImageName}:latest || true"
                   }

                   // 3. 1번에서 태그가 previousBuildId로 변경된 도커 이미지를 원격 도커 허브에 푸시
                   docker.withRegistry('', registryCredential) {
                       sh "docker push ${env.fullImageName}:${previousBuildId} || true"
                   }
       
                   // 4. 로컬에서 previousBuildId 태그에 해당하는 이미지 삭제
                   sh "docker rmi ${env.fullImageName}:${previousBuildId} || true"

                   // 5. 새로 생성되는 도커 이미지의 태그를 latest로 설정하고 푸시
                   dockerImage = docker.build("${env.fullImageName}:latest")
                   docker.withRegistry('', registryCredential) {
                       dockerImage.push()
                   }

                }
            }
        }

    }

    post {
        success {
            slackSend(channel: '#jenkins', color: '#00FF00', message: """:white_check_mark: Prod 서버 CI/CD 파이프라인 성공 : ${env.JOB_NAME} [${env.BUILD_NUMBER}] 확인 : (${env.BUILD_URL})""")
        }

        failure {
            slackSend(channel: '#jenkins', color: '#FF0000', message: """:octagonal_sign: Prod 서버 CI/CD 파이프라인 실패 : ${env.JOB_NAME} [${env.BUILD_NUMBER}] 확인 : (${env.BUILD_URL})""")
        }
    }
}
