#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <pthread.h>
#define port 8080
void *readFromSock(void *sock_fd)
{
    char inputBuff[1024] = {0};
    ssize_t bytesRead;
    while ((bytesRead = read(*(int *)sock_fd, inputBuff, sizeof(inputBuff) - 1)) > 0) {
        inputBuff[bytesRead] = '\0';
        printf("%s", inputBuff);
    }
    return NULL;
}
void *writeToSock(void *sock_fd)
{
    char outputBuff[1024] = {0};
    while (fgets(outputBuff, sizeof(outputBuff), stdin) != NULL)
    {

        write(*(int *)sock_fd, outputBuff, strlen(outputBuff));

        if (strcmp(outputBuff, "./Exit\n") == 0)
            break;
    }
    return NULL;
}
int main()
{
    pthread_t writerThread, readerThread;
    struct sockaddr_in address;
    int server_fd, client_fd;
    socklen_t addrlen = sizeof(address);
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    {
        perror("failed to create socket");
        exit(0);
    }
    address.sin_port = htons(port);
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    if ((bind(server_fd, (struct sockaddr *)&address, addrlen)) < 0)
    {
        perror("failed to bind socket");
        exit(0);
    }
    if (listen(server_fd, 10) < 0)
    {
        perror("failed to set socket to port");
        exit(0);
    }
    if ((client_fd = accept(server_fd, (struct sockaddr *)&address, &addrlen)) < 0)
    {
        perror("failed to connect ");
        exit(0);
    }
    if (pthread_create(&readerThread, NULL, readFromSock, &client_fd) != 0)
    {
        perror("failed to start reader thread");
        exit(0);
    }
    if (pthread_create(&writerThread, NULL, writeToSock, &client_fd) != 0)
    {
        perror("failed to start writer thread");
        exit(0);
    }
    pthread_join(readerThread, NULL);
    pthread_join(writerThread, NULL);
    close(server_fd);
    close(client_fd);
    return 0;
}
