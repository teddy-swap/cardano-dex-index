FROM openjdk:8-jre-slim as builder
RUN apt-get update && \
    apt-get install -y --no-install-recommends apt-transport-https apt-utils bc dirmngr gnupg && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 && \
    apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends sbt
COPY . /cardano-dex-index
WORKDIR /cardano-dex-index
RUN sbt cardano-rates-resolver/assembly
RUN mv `find . -name rates-resolver.jar` /rates-resolver.jar
CMD ["/usr/bin/java", "-jar", "/rates-resolver.jar"]

FROM openjdk:8-jre-slim
COPY --from=builder /rates-resolver.jar /rates-resolver.jar
COPY --from=builder /cardano-dex-index/modules/rates-resolver/src/main/resources/base.conf /tmp/base.conf
ENV CONFIG_PATH=/tmp/base.conf
ENTRYPOINT java -Xms1G -Xmx2G -jar /rates-resolver.jar $CONFIG_PATH