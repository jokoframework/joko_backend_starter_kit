FROM maven:3.9.6-eclipse-temurin-17

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
COPY pom.xml pom.xml
COPY src src
RUN apt-get update && apt-get install -y --no-install-recommends dos2unix \
    && dos2unix /usr/local/bin/entrypoint.sh \
    && chmod +x /usr/local/bin/entrypoint.sh \
    && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["bash"]
