def slackNotifier(String buildResult) {
  if ( buildResult == "SUCCESS" ) {
    slackSend ( color: "good", 
    teamDomain: '${teamDomain}', 
    channel: '#${SlackChannel}',
    tokenCredentialId: '${SlackCredentials}',
    message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was successful")
  }
  else if( buildResult == "FAILURE" ) { 
    slackSend( color: "danger", 
    teamDomain: '${teamDomain}', 
    channel: '#${SlackChannel}',
    tokenCredentialId: '${SlackCredentials}',
    message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was failed")
  }
  else if( buildResult == "UNSTABLE" ) { 
    slackSend( color: "warning", 
      teamDomain: '${teamDomain}', 
      channel: '#${SlackChannel}',
      tokenCredentialId: '${SlackCredentials}',
      message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was unstable")
  }
  else if( buildResult == "null" ) { 
    slackSend( color: "warning", 
      teamDomain: '${teamDomain}', 
      channel: '#${SlackChannel}',
      tokenCredentialId: '${SlackCredentials}',
      message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} finished with unknown status")
  }
}

def startMsg() {
  commit_desc = sh(script: "git log -1 --format='format:%s'", returnStdout: true).trim()
  slackSend(teamDomain: '${teamDomain}', 
    channel: '#${SlackChannel}',
    tokenCredentialId: '${SlackCredentials}',
    message: "Job ${env.JOB_NAME} <${env.BUILD_URL}|build ${env.BUILD_DISPLAY_NAME}> started by ${env.BUILD_USER}\n====\n${commit_desc}")

}

@NonCPS

def configFromBranch(branch) {
    def env_dev = [
    ]
    def env_qa = [
    ]
    def env_stg = [
    ]
    def env_prd = [
    ]
    if (branch ==~ /(develop)/) { 
        return [
            shouldTest: true,
            shouldDeploy: true,
            env: 'dev',
            tag: 'dev',
            deployments: [env_dev]
        ]
    }    
    if (branch ==~ /(qa)/) { 
        return [
            shouldTest: true,
            shouldDeploy: true,
            env: 'qa',
            tag: 'qa',
            deployments: [env_qa]
        ]
    }
    if (branch ==~ /(staging)/) {
        return [
            shouldTest: true,
            shouldDeploy: true,
            env: 'staging',
            tag: 'stg',
            deployments: [env_stg]
        ]
    }
    else if (branch ==~ /(master)/) {
        return [
            shouldTest: true,
            shouldDeploy: false,
            env: 'prd',
            tag: 'prd',
            deployments: [env_prd]
        ]
    }
    else {
        return [
            shouldTest: false,
            shouldDeploy: false,
            tag: '-',
            deployments: []
        ]
    }
}

pipeline {
    agent none
    
    environment {
        CONFIG = configFromBranch(BRANCH_NAME)
        SHOULD_TEST = "${CONFIG.shouldTest}"
        SHOULD_DEPLOY = "${CONFIG.shouldDeploy}"
        ENV = "${CONFIG.env}"
        TAG = "${CONFIG.tag}"

        PROJECT_NAME="${PROJECT_NAME}"
        GIT_URL="https://machina@bitbucket.org/machina/${PROJECT_NAME}.git"

    }

    stages {
        stage ('CI') {
            agent {
                label 'TestContainer'
            }
            stages {
                stage('SCM - Checkout') {
                    steps{
                        cleanWs()
                        git branch: BRANCH_NAME, 
                        credentialsId: "bitb-machina", 
                        url: GIT_URL
                        echo 'SCM Checkout'
                    }
                }
                stage ('Test & Publish QA Reports'){
                    when {
                      expression { SHOULD_TEST == 'true' }
                    }
                    steps{
                        echo 'Test & Publish QA Reports'
                    }
                }
            }
        }
        stage ('CD') {
            stages {
                stage ('Deploy') {
                    agent {
                        label 'Build'
                    }
                    stages{
                        stage ('YARN Build'){
                            steps{
                                sh "eval \$(ssh-agent); ssh-add ~/.ssh/id_rsa"
                                sh "yarn install"
                                sh "yarn build:${ENV}"
                            }
                        }
                        stage ('AWS s3 Bucket Deploy Updates') {
                            when {
                              expression { SHOULD_DEPLOY == 'true' }
                            }                  
                            steps {
                                //withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'machina-ecr', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
                                // Atualiza o Bucket existente as alterações da app
                                sh "aws s3 cp build s3://${PROJECT_NAME}-${ENV}/ --recursive"                                }

                                /* Cria o Bucket
                                    sh "aws s3 create-bucket --bucket s3://${PROJECT_NAME}-${ENV}
                                Cria a primeira Distro
                                    sh "aws cloudfront create-distribution --origin-domain-name ${PROJECT_NAME}.s3.amazonaws.com --default-root-object index.html
                                Lista as distros
                                    sh "aws cloudfront get-distribution-config --list
                                Clona a configuração da Distro desejada
                                    sh "aws cloudfront get-distribution --id ${DIST-ID} >> ${PROJECT_NAME}-${ENV}.json"
                                    sh "aws cloudfront create-distribution --distribution-config file://${PROJECT_NAME}-${ENV}.json
                                */
                                
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        success {
            script{
                slackNotifier('SUCCESS')
            }
        }
        failure{
            script{
                slackNotifier('FAILURE')
            }
        }
        unstable{
            script{
                slackNotifier('UNSTABLE')
            }
        }
        aborted{
            script{
                slackNotifier('ABORTED')
            }
        }
    }
}
