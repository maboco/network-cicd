pipeline {
    agent {
        kubernetes {
            yaml """
                apiVersion: v1
                kind: Pod
                metadata:
                  name: clab-dind
                spec:
                  containers:
                  - name: containerlab
                    image: maboco/clab-dind:latest
                    securityContext:
                      privileged: true
                """
        }
    }
    parameters {
        choice(
            name: 'TOPOLOGY', 
            choices: ['pair'], 
            description: 'Network Topology')
    }
    stages {
        stage('Checkout Infra Repo') {
            steps {
                dir('infra') {
                    git branch: "master", url: "https://github.com/maboco/network-iacs.git"
                }
            }
        }
    }
}