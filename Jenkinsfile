pipeline {
    agent any

    stages {
        stage('1. Code Checkout') {
            steps {
                // 깃허브에서 최신 소스코드를 다운로드합니다.
                checkout scm
            }
        }

        stage('2. Build & Deploy via SSH to Swarm') {
            steps {
                // 문법 오류를 해결한 안전한 SSH 전송 명령어
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'curebridge-swarm-manager', // 하빈님의 실제 시스템 설정 이름
                        verbose: true,
                        transfers: [
                            sshTransfer(
                                execCommand: '''
                                    # [AWS EC2 내부에서 실행될 명령어 라인]
                                    cd /home/ubuntu/CureBridge

                                    # 1. 최신 코드 깃 풀 당기기 (현재 작업 중인 develophb 브랜치로 pull 하려면 origin develophb로 변경 가능)
                                    git pull origin develophb

                                    # 2. MSA 개별 서비스 빌드 및 이미지 빌드 슛!
                                    docker compose -f docker-stack.yml build

                                    # 3. 도커 스웜 스택 업데이트 실행
                                    docker stack deploy -c docker-stack.yml curebridge

                                    # 4. 유령 찌꺼기 이미지 청소
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