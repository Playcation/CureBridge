pipeline {
    agent any

    environment {
        // 젠킨스 Credentials 금고에 조금 전 등록한 ID와 정확히 매치시킵니다.
        DOCKER_CRED_ID = 'dockerhub_token'
    }

    stages {
        stage('1. Code Checkout') {
            steps {
                checkout scm
            }
        }

        stage('2. Build CommonModule & Services') {
            steps {
                // 젠킨스 내부에서 Gradle 빌드를 안전하게 실행합니다.
                sh 'chmod +x gradlew'
                sh './gradlew :CommonModule:compileJava --no-daemon'
                sh './gradlew build -x test --no-daemon'
            }
        }

        stage('3. Docker Build & Push to Hub') {
            steps {
                // 금고에서 자동으로 Username($DOCKER_USER)과 Password($DOCKER_PASS)를 꺼내와 주입합니다.
                withCredentials([usernamePassword(credentialsId: "${DOCKER_CRED_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {

                    // 도커 허브 안전하게 로그인
                    sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"

                    // $DOCKER_USER 변수를 활용해 내 도커 허브 계정에 맞춤형으로 빌드 및 푸시를 실행합니다.
                    sh "docker build -t \$DOCKER_USER/curebridge-gateway:latest -f GateWay/Dockerfile ."
                    sh "docker build -t \$DOCKER_USER/curebridge-content:latest -f ContentService/Dockerfile ."
                    sh "docker build -t \$DOCKER_USER/curebridge-chat:latest -f Chat/Dockerfile ."
                    sh "docker build -t \$DOCKER_USER/curebridge-member:latest -f MemberService/Dockerfile ."
                    sh "docker build -t \$DOCKER_USER/curebridge-app:latest -f AppService/Dockerfile ."

                    sh "docker push \$DOCKER_USER/curebridge-gateway:latest"
                    sh "docker push \$DOCKER_USER/curebridge-content:latest"
                    sh "docker push \$DOCKER_USER/curebridge-chat:latest"
                    sh "docker push \$DOCKER_USER/curebridge-member:latest"
                    sh "docker push \$DOCKER_USER/curebridge-app:latest"
                }
            }
        }

        stage('4. Deploy via SSH to Swarm') {
            steps {
                // 배포 단계에서도 금고에서 꺼낸 계정 정보를 활용해 EC2 스웜 매니저에 최신 이미지를 반영합니다.
                withCredentials([usernamePassword(credentialsId: "${DOCKER_CRED_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sshPublisher(publishers: [
                        sshPublisherDesc(
                            configName: 'curebridge-swarm-manager',
                            verbose: true,
                            transfers: [
                                sshTransfer(
                                    execCommand: """
                                        set -e
                                        cd ~/curebridge

                                        # 도커 허브에서 최신 이미지 강제 풀
                                        docker pull \$DOCKER_USER/curebridge-gateway:latest
                                        docker pull \$DOCKER_USER/curebridge-content:latest
                                        docker pull \$DOCKER_USER/curebridge-chat:latest
                                        docker pull \$DOCKER_USER/curebridge-member:latest
                                        docker pull \$DOCKER_USER/curebridge-app:latest

                                        # 스웜 스택 업데이트 실행
                                        docker stack deploy -c docker-stack.yml curebridge --with-registry-auth

                                        # 찌꺼기 청소
                                        docker system prune -f
                                    """,
                                    execTimeout: 120000
                                )
                            ]
                        )
                    ])
                }
            }
        }
    }
}