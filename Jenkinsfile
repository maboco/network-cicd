pipeline {
    agent {
        kubernetes {
            yaml """
                apiVersion: v1
                kind: Pod
                spec:
                  volumes:
                    - name: workspace-volume
                      emptyDir: {}
                  containers:
                    - name: jnlp
                      image: jenkins/inbound-agent:latest
                      volumeMounts:
                        - name: workspace-volume
                          mountPath: /home/jenkins/agent
                    - name: containerlab
                      image: maboco/clab-dind:latest
                      tty: true
                      securityContext:
                        privileged: true
                      volumeMounts:
                        - name: workspace-volume
                          mountPath: /home/jenkins/agent
                    - name: ansible
                      image: alpine/ansible:latest
                      tty: true
                      volumeMounts:
                        - name: workspace-volume
                          mountPath: /home/jenkins/agent       
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