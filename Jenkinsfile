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
                // 아까 테스트 커넥션 성공했던 매니저 노드로 원격 접속합니다.
                sshPublisher(publishers: [
                    sshPublisherDetail(
                        configName: 'curebridge-swarm-manager',
                        transfers: [
                            sshTransfer(
                                cleanRemote: false,
                                excludes: '',
                                execCommand: '''
                                    # [AWS EC2 내부에서 실행될 명령어 라인]
                                    cd /home/ubuntu/CureBridge

                                    # 1. 최신 코드 깃 풀 당기기
                                    git pull origin master

                                    # 2. MSA 개별 서비스 빌드 및 이미지 빌드 슛!
                                    # 하빈님의 스웜 구조에 맞게 묶어서 빌드하거나 스택을 갱신합니다.
                                    # (예시: docker compose로 이미지를 새로 빌드하고 스택 배포)
                                    docker compose -f docker-stack.yml build

                                    # 3. 도커 스웜 스택 업데이트 실행
                                    docker stack deploy -c docker-stack.yml curebridge

                                    # 4. 유령 찌꺼기 이미지 청소
                                    docker system prune -f
                                ''',
                                execTimeout: 120000,
                                flattenFiles: false,
                                makeEmptyDirs: false,
                                noDefaultExcludes: false,
                                patternSeparator: '[, ]+',
                                remoteDirectory: '',
                                remoteDirectorySDF: false,
                                removePrefix: '',
                                sourceFiles: ''
                            )
                        ],
                        usePromotionTimestamp: false,
                        useWorkspaceInPromotion: false,
                        verbose: true
                    )
                ])
            }
        }
    }
}