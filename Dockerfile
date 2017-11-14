FROM freedomkk/tomcat-maven:8

ENV BUILD_DIR /tmp/lodestar/

RUN mkdir $BUILD_DIR && \
  cd $BUILD_DIR && \
  git clone https://github.com/amalic/lodestar.git && \
  cd lodestar && \
  mvn clean package && \
  cp web-ui/target/lodestar.war $CATALINA_HOME/webapps/ && \
  rm -rf $BUILD_DIR
  
CMD ["bin/catalina.sh", "run"]

EXPOSE 8080
