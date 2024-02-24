FROM openjdk:17
COPY /out/production/Project1 /project1
WORKDIR /project1
# cmd to run server locally - java server.ServerApp 1111 5555
EXPOSE 1025
CMD ["java", "server.TCPServer", "1025"]