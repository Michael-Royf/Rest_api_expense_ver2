

use command--->    docker-compose up -d
for email ---> http://localhost:1080


если запускаешь проект на локальной машине, запусти maildev отдельно, а docker-compose.yml закоментируй maildev and  backend:
docker run -p 1080:1080 -p 1025:1025 maildev/maildev    ---> for email sender
open --->  http://localhost:1080/


для dockerhub
./mvnw clean install  -P jib-push-to-dockerhub -D app.image.tag=3
