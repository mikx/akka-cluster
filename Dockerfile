FROM java:8

EXPOSE 8080 2552

ADD target/universal/weavecluster-1.0.zip /tmp/weavecluster-1.0.zip
RUN unzip /tmp/weavecluster-1.0.zip -d /tmp

ENTRYPOINT /tmp/weavecluster-1.0/bin/weavecluster
