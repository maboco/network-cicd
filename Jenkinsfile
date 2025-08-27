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
                      command:
                        - 'sh'
                        - '-c'
                        - 'sleep infinity'
                      volumeMounts:
                        - name: workspace-volume
                          mountPath: /home/jenkins/agent       
                """
        }
    }
    parameters {
        choice(
            name: 'TOPOLOGY', 
            choices: ['pair', 'clos'], 
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
        stage('Deploy dev infra') {
          steps {
            dir('infra') {
              script {
                def workspace = pwd()
                def devFolder = "${workspace}/${params.TOPOLOGY}/infra/dev"

                container('ansible') {
                  sh """
                    ansible localhost \
                    -m ansible.builtin.template \
                    -a "src=${devFolder}/templates/topology.j2 dest=${devFolder}/templates/topology.clab.yaml" \
                    -e "@${devFolder}/inventory.yaml"
                  """
                }

                container('containerlab') {
                  sh """
                    containerlab deploy -f ${devFolder}/templates/topology.clab.yaml
                  """
                }
              }
            }
          }
        }
        stage('Generate config') {
          steps {
            dir('config') {
              container('ansible') {
                script {
                  sh """
                    ansible-playbook ${params.TOPOLOGY}/config/render-configs.yaml
                  """
                }
              }
            }
          }
        }
    }
}