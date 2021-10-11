1 - Use todos os pontos de presença (melhor desempenho)  - Price Class All (Maior Preço, Menor Desempenho)
2 - Use apenas América do Norte e Europa - Price Class 100 (Menor Preço, Menor Desempenho)
3 - Use a América do Norte, Europa, Ásia, Oriente Médio e África - Price Class 200 (Médio Preço, Médio Desempenho)
Opção 2 para ambiente não-produtivo e buscar menor custo.

*Página oficial da documentação: Choosing the price class for a CloudFront distribution - Amazon CloudFront
:cloud:      &  => Tabela de preços e cobranças: https://aws.amazon.com/pt/cloudfront/pricing/


Obrigatório!
Criar um Origin Access Identity para acesso ao s3 Bucket ($s3Bucket), que será exposto.

*Apontar o OAI e apenas executar com a criação automática, caso tenha problemas confira as permissões JSON
diretamente no s3 Bucket

Para utilizar um DNS personalizado, eg.: Domínio alternativo:  https://$APLICATIVO.env-preprodb.com
Crie um certificado SSL no console para isso.

Para atualização e criação das Distribuições, recomenda-se as seguintes configurações
Nessa url criar uma regra de invalidação, com o seguinte conteúdo, como na imagem-1:
  https://console.aws.amazon.com/cloudfront/invalidations

Valor para configuração: "/*" 

*/imagem-1

Distribuição CloudFront $APLICATIVO:
Domínio aleatório:  dumydemyfoo58.cloudfront.net
Domínio alternativo:  https://$APLICATIVO.env-preprodb.com
Origem s3:  https://$APLICATIVO.s3.sa-east-1.amazonaws.com


Podemos usar um arquivo pronto para criar uma ditro:
Atualiza o Bucket existente as alterações da app
 sh "aws s3 cp build s3://${PROJECT_NAME}-${ENV}/ --recursive"                                }
 Cria o Bucket
     sh "aws s3 create-bucket --bucket s3://${PROJECT_NAME}-${ENV}
 Cria a primeira Distro
     sh "aws cloudfront create-distribution --origin-domain-name ${PROJECT_NAME}.s3.amazonaws.com --default-root-object index.html
 Lista as distros
     sh "aws cloudfront get-distribution-config --list
 Clona a configuração da Distro desejada
     sh "aws cloudfront get-distribution --id ${DIST-ID} >> ${PROJECT_NAME}-${ENV}.json"
     sh "aws cloudfront create-distribution --distribution-config file://${PROJECT_NAME}-${ENV}.json
