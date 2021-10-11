pipeline {
  agent any
  stages {
    stage('Deploy to s3') {
      when {
        branch '*'
      }
      steps {
        slackSend channel: "#<channel>", message: "Deployment Starting: ${env.JOB_NAME}, build number ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
        echo 'Deploying to Bucky a AWS s3 bucket.'
        withAWS(region:'<AWS Region: us-east-1>', credentials:'aws') {
          s3Create(bucket: '<FromJenkinsBucky>', path:'**/*')
          s3Delete(bucket: '<FromJenkinsBucky>', path:'**/*')
          s3Upload(bucket: '<FromJenkinsBucky>', includePathPattern:'**/*')
        }
      }
    }
  }
  post {
    success {
      slackSend channel: "#<channel>", color: "good", message: "Deployment Complete: ${env.JOB_NAME}, build number ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
    }
    failure {
      slackSend channel: "#<channel>", color: "danger", message: "Deployment Failed: ${env.JOB_NAME}, build number ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
    }
    // always {
    //   slackSend ...
    // }
  }
}
