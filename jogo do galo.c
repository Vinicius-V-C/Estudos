#include <stdio.h>
#include <stdlib.h>

void iniciarTabuleiro(char tabuleiro[3][3]) {
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            tabuleiro[i][j] = ' ';
        }
    }
}

void exibirTabuleiro(char tabuleiro[3][3]) {
    printf("\nTabuleiro Atual:\n");
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            printf("%c", tabuleiro[i][j]);
            if (j < 2) {
                printf(" | ");
            }
        }
        printf("\n");
        if (i < 2) {
            printf("---------\n");
        }
    }
}

int verificarVencedor(char tabuleiro[3][3], char jogador) {
    for (int i = 0; i < 3; i++) {
        if (tabuleiro[i][0] == jogador && tabuleiro[i][1] == jogador && tabuleiro[i][2] == jogador) {
            return 1;
        }
        if (tabuleiro[0][i] == jogador && tabuleiro[1][i] == jogador && tabuleiro[2][i] == jogador) {
            return 1;
        }
    }
    if (tabuleiro[0][0] == jogador && tabuleiro[1][1] == jogador && tabuleiro[2][2] == jogador) {
        return 1;
    }
    if (tabuleiro[0][2] == jogador && tabuleiro[1][1] == jogador && tabuleiro[2][0] == jogador) {
        return 1;
    }
    return 0;
}

void jogar(char tabuleiro[3][3]) {
    char jogador = 'X';
    int linha, coluna;
    int jogadas = 0;

    while (1) {
        printf("\nJogador %c\nLinha: ", jogador);
        scanf("%d", &linha);
        linha--;
        printf("Coluna: ");
        scanf("%d", &coluna);
        coluna--;

        if (linha >= 0 && linha < 3 && coluna >= 0 && coluna < 3 && tabuleiro[linha][coluna] == ' ') {
            tabuleiro[linha][coluna] = jogador;
            jogadas++;
            exibirTabuleiro(tabuleiro);

            if (verificarVencedor(tabuleiro, jogador)) {
                printf("Jogador %c venceu!\n", jogador);
                break;
            } else if (jogadas == 9) {
                printf("Empate!\n");
                break;
            }

            jogador = (jogador == 'X') ? 'O' : 'X';
        } else {
            printf("Jogada invalida. Tente novamente.\n");
        }
    }
}

int main() {
    char tabuleiro[3][3];
    iniciarTabuleiro(tabuleiro);
    jogar(tabuleiro);
    system("pause");
    return 0;
}
