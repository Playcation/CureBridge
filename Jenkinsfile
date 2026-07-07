pipeline {
    agent any

    stages {
        stage('1. Code Checkout') {
            steps {
                // 젠킨스가 스크립트를 읽기 위해 깃에서 가져옵니다.
                checkout scm
            }
        }

        stage('2. Deploy via SSH to Swarm') {
            steps {
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'curebridge-swarm-manager',
                        verbose: true,
                        transfers: [
                            sshTransfer(
                                execCommand: '''
                                    set -e
                                    cd ~/curebridge

                                    # [1] 만약 환경변수 파일들이 매번 동적으로 바뀌어야 한다면 서버에 생성해 줍니다.
                                    # (이미 서버에 고정되어 있다면 이 단계는 패스해도 됩니다)
                                    # echo "${GATEWAY_ENV}" > gateway.env
                                    # echo "${CONTENT_ENV}" > content.env

                                    # [2] GitHub Actions가 올려둔 최신 이미지 Docker Hub에서 강제로 새로 땡겨오기
                                    # (username 부분은 본인의 도커 허브 ID로 채워주세요)
                                    docker pull 도커허브_ID/curebridge-gateway:latest
                                    docker pull 도커허브_ID/curebridge-content:latest
                                    docker pull 도커허브_ID/curebridge-chat:latest
                                    docker pull 도커허브_ID/curebridge-member:latest
                                    docker pull 도커허브_ID/curebridge-app:latest

                                    # [3] 도커 스웜 스택 업데이트 (GitHub Actions와 동일한 명령어)
                                    docker stack deploy -c docker-stack.yml curebridge --with-registry-auth

                                    # [4] 사용하지 않는 유령 이미지 정리
                                    docker system prune -f
                                ''',
                                execTimeout: 120000
                            )
                        ]
                    )
                ])
            }
        }
    }
}