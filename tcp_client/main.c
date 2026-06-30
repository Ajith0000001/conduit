#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <arpa/inet.h>
#include <unistd.h>

#define PORT 9000

#define CREATE_ACCOUNT 1
#define BLOCK_ACCOUNT  2
#define TRANSFER        3

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

void select_user_type(int socketfd);

void starter(int socketfd)
{
    printf("******************************\n");
    select_user_type(socketfd);
}

void select_user_type(int socketfd)
{
    uint32_t choice;

    printf("1. Create Account\n");
    printf("2. Block Account\n");
    printf("3. Transfer Money\n");
    printf("0. Exit\n");

    printf("Enter choice : ");
    scanf("%u", &choice);

    PacketHeader header;

    switch (choice)
    {
        case CREATE_ACCOUNT:
        {
            AccountCreation ac;

            printf("Initial Deposit : ");
            scanf("%u", &ac.amount);

            ac.amount = htonl(ac.amount);

            header.len = htonl(sizeof(AccountCreation));
            header.type = htonl(CREATE_ACCOUNT);

            send(socketfd, &header, sizeof(header), 0);
            send(socketfd, &ac, sizeof(ac), 0);

            break;
        }

        case BLOCK_ACCOUNT:
        {
            BlockAccount b;

            printf("Account Id : ");
            scanf("%u", &b.account_id);

            b.account_id = htonl(b.account_id);

            header.len = htonl(sizeof(BlockAccount));
            header.type = htonl(BLOCK_ACCOUNT);

            send(socketfd, &header, sizeof(header), 0);
            send(socketfd, &b, sizeof(b), 0);

            break;
        }

        case TRANSFER:
        {
            TransferMoney t;

            printf("Sender Account : ");
            scanf("%36s", t.sender_uuid);

            printf("Receiver Account : ");
            scanf("%36s", t.receiver_uuid);

            printf("Amount : ");
            scanf("%u", &t.amount);


            header.len = htonl(78);
            header.type = htonl(TRANSFER);

            send(socketfd, &header, sizeof(header), 0);

            send(socketfd, t.sender_uuid, 37, 0);
            send(socketfd, t.receiver_uuid, 37, 0);

            uint32_t amt = htonl(t.amount);
            send(socketfd, &amt, sizeof(amt), 0);

            break;
        }

        case 0:
            close(socketfd);
            exit(0);

        default:
            printf("Invalid Choice\n");
    }
}

int main()
{
    int socketfd;

    struct sockaddr_in server_addr;

    socketfd = socket(AF_INET, SOCK_STREAM, 0);

    if (socketfd < 0)
    {
        perror("socket");
        return 1;
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    inet_pton(AF_INET, "127.0.0.1", &server_addr.sin_addr);

    if (connect(socketfd,
                (struct sockaddr *)&server_addr,
                sizeof(server_addr)) < 0)
    {
        perror("connect");
        return 1;
    }

    starter(socketfd);

    close(socketfd);

    return 0;
}