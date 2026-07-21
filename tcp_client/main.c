#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>

#define PORT 9000

#define CREATE_ACCOUNT 1
#define BLOCK_ACCOUNT  3
#define TRANSFER       2

#define CREATE_ACCOUNT_RESPONSE 101

typedef struct
{
    uint32_t amount;
} AccountCreation;

typedef struct
{
    uint32_t account_id;
} BlockAccount;

typedef struct
{
    char sender_uuid[37];
    char receiver_uuid[37];
    uint32_t amount;
} TransferMoney;

typedef struct
{
    uint32_t len;
    uint32_t type;
} PacketHeader;

int read_n(int sockfd, void *buf, int len)
{
    int total = 0;

    while (total < len)
    {
        int n = recv(
            sockfd,
            (char *)buf + total,
            len - total,
            0);

        if (n <= 0)
            return n;

        total += n;
    }

    return total;
}

int send_n(int sockfd, const void *buf, int len)
{
    int total = 0;

    while (total < len)
    {
        int n = send(
            sockfd,
            (const char *)buf + total,
            len - total,
            0);

        if (n <= 0)
            return n;

        total += n;
    }

    return total;
}
void transfer_money(int socketfd)
{
    TransferMoney t;
    PacketHeader header;

    printf("Sender Account Number : ");
    scanf("%36s", t.sender_uuid);

    printf("Receiver Account Number : ");
    scanf("%36s", t.receiver_uuid);

    printf("Amount : ");
    scanf("%u", &t.amount);

    header.len = htonl(78);
    header.type = htonl(TRANSFER);

    uint32_t amount = htonl(t.amount);

    send_n(socketfd, &header, sizeof(header));
    send_n(socketfd, t.sender_uuid, 37);
    send_n(socketfd, t.receiver_uuid, 37);
    send_n(socketfd, &amount, sizeof(amount));

    printf("Transfer request sent\n");
}

void create_account(int socketfd)
{
    AccountCreation ac;
    PacketHeader header;

    printf("Initial Deposit : ");
    scanf("%u", &ac.amount);

    ac.amount = htonl(ac.amount);

    header.len = htonl(sizeof(AccountCreation));
    header.type = htonl(CREATE_ACCOUNT);

    send(socketfd, &header, sizeof(header), 0);
    send(socketfd, &ac, sizeof(ac), 0);

    printf("Request sent...\n");

    PacketHeader response;

    if (read_n(socketfd,
               &response,
               sizeof(response)) <= 0)
    {
        printf("Server disconnected\n");
        return;
    }

    response.len = ntohl(response.len);
    response.type = ntohl(response.type);

    printf("Response Type : %u\n", response.type);

    if (response.type == CREATE_ACCOUNT_RESPONSE)
    {
        char *accountNumber =
            malloc(response.len + 1);

        if (accountNumber == NULL)
        {
            printf("Memory allocation failed\n");
            return;
        }

        if (read_n(socketfd,
                   accountNumber,
                   response.len) <= 0)
        {
            printf("Failed to receive payload\n");
            free(accountNumber);
            return;
        }

        accountNumber[response.len] = '\0';

        printf("\n");
        printf("================================\n");
        printf("Account Created Successfully\n");
        printf("Account Number : %s\n",
               accountNumber);
        printf("================================\n");

        free(accountNumber);
    }
    else
    {
        printf("Unexpected response type\n");
    }
}

void menu(int socketfd)
{
    while (1)
    {
        int choice;

        printf("\n");
        printf("1. Create Account\n");
        printf("2. Transfer Money\n");
        printf("0. Exit\n");
        printf("Choice : ");

        scanf("%d", &choice);

        switch (choice)
        {
        case CREATE_ACCOUNT:
            create_account(socketfd);
            break;
        
        case TRANSFER:
            transfer_money(socketfd);
            break;

        case 0:
            close(socketfd);
            exit(0);

        default:
            printf("Invalid choice\n");
        }
    }
}

int main()
{
    int socketfd;

    struct sockaddr_in server_addr;

    socketfd = socket(AF_INET,
                      SOCK_STREAM,
                      0);

    if (socketfd < 0)
    {
        perror("socket");
        return 1;
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);

    inet_pton(AF_INET,
              "127.0.0.1",
              &server_addr.sin_addr);

    if (connect(socketfd,
                (struct sockaddr *)&server_addr,
                sizeof(server_addr)) < 0)
    {
        perror("connect");
        close(socketfd);
        return 1;
    }

    printf("Connected to server\n");

    menu(socketfd);

    close(socketfd);

    return 0;
}