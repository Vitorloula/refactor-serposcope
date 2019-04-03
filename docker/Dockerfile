FROM java:8-jre

<<<<<<< HEAD
ENV SERPOSCOPE_VERSION 2.11.0
=======
ARG SERPOSCOPE_VERSION

ENV SERPOSCOPE_VERSION ${SERPOSCOPE_VERSION:-2.10.0}
>>>>>>> cfb73ab ([Dockerfile] use ARG to pass the version as a build argument)

COPY serposcope /etc/default/serposcope
RUN wget https://serposcope.serphacker.com/download/${SERPOSCOPE_VERSION}/serposcope_${SERPOSCOPE_VERSION}_all.deb -O /tmp/serposcope.deb
RUN dpkg --force-confold -i /tmp/serposcope.deb
RUN rm /tmp/serposcope.deb

VOLUME /var/lib/serposcope/

EXPOSE 7134

COPY entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
