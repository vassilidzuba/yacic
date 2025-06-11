pipeline(environment) {
        name = "maven"
        description = "build java application using maven"

        podmanstep {
            id = "clone"
            description = "clone repository"
            image = "docker.io/alpine/git"
            command = "--name @{ACTIONID}-@{PROJECT}-@{BRANCHDIR} -v \${HOME}:/root -v @{DATAAREA}:/git @{IMAGE} clone -b \"@{BRANCHNAME}\" @{REPO}"
        }

        podmanstep {
            id = "build"
            description = "build package using maven"
            image = "maven:3.9.9-amazoncorretto-21-alpine"
            command = "--name @{ACTIONID}-@{PROJECT}-@{BRANCHDIR} -v \"\$HOME/.m2:/root/.m2\" -v \"@{DATAAREA}/@{PROJECT}\":/usr/src/@{PROJECT} -w /usr/src/@{PROJECT} @{IMAGE} mvn clean package"
        }

        podmanstep {
            id = "sonar"
            description = "execute sonarqube"
            image = "192.168.0.20:5000/gradle-sonar:java21"
            setup = "rm -rf @{DATAAREA}/@{PROJECT}; mkdir -p @{DATAAREA};"
            command = "--name @{ACTIONID}-@{PROJECT}-@{BRANCHDIR} --secret sonar-token,type=env,target=token -v \"\$HOME/.m2:/root/.m2\" -v \"@{DATAAREA}/@{PROJECT}\":/usr/src/@{PROJECT} -w /usr/src/@{PROJECT} @{IMAGE}"
        }

        shellstep {
            id = "deploy_javadoc"
            description = "deploy javadoc to nginx"
            command = "cp @{DATAAREA}/@{PROJECT}/target/*-javadoc.jar /home/podman/nginx/javadoc; cd /home/podman/nginx; ./launch-nginx.sh; systemctl --user restart nginx"
        }

        podmanstep {
            id = "deploy_nexus"
            description = "deploy to nexus"
            image = "maven:3.9.9-amazoncorretto-21-alpine"
            command = "--name @{ACTIONID}-@{PROJECT}-@{BRANCHDIR} -v \"\$HOME/.m2:/root/.m2\" -v \"@{DATAAREA}/@{PROJECT}\":/usr/src/@{PROJECT} -w /usr/src/@{PROJECT} @{IMAGE} mvn -Dmaven.javadoc.skip=true deploy"
        }

        shellstep {
            id = "build_image"
            description = "build docker image"
            command = "cd @{DATAAREA}/@{PROJECT};  podman build -t @{DOCKERTAG} -f Dockerfile"
        }
    }