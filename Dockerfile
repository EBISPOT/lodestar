FROM freedomkk/tomcat-maven:8

ENV BUILD_DIR /tmp/lodestar/

RUN mkdir $BUILD_DIR && \
  cd $BUILD_DIR && \
  git clone https://github.com/EBISPOT/lodestar.git

WORKDIR $BUILD_DIR/lodestar/

ADD ./config-docker/lode.properties web-ui/src/main/resources/lode.properties

RUN mvn clean package && \
  cp web-ui/target/lodestar.war $CATALINA_HOME/webapps/ && \
  rm -rf $BUILD_DIR

WORKDIR $CATALINA_HOME
  
CMD ["bin/catalina.sh", "run"]

EXPOSE 8080
