FROM freedomkk/tomcat-maven

ENV WEBSITE_HOME /tmp/lodestar/

RUN mkdir $WEBSITE_HOME && \
  cd $WEBSITE_HOME && \
  git clone https://github.com/amalic/lodestar.git && \
  cd lodestar && \
  mvn clean package && \
  cp web-ui/target/lodestar.war $CATALINA_HOME/webapps/
  

CMD ["bin/catalina.sh", "run"]

EXPOSE 8080
