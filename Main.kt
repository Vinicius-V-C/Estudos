import java.io.File
const val SIMBOLO_HUMANO = "H"
const val SIMBOLO_COMPUTADOR = "C"
const val VAZIO = ""
const val NULL_STRING = "null"
const val BALAO_VERMELHO = "\u001b[31m\u03D9\u001b[0m"
const val BALAO_AZUL = "\u001b[34m\u03D9\u001b[0m"
const val COMANDO_GRAVAR = "Gravar"
const val COMANDO_EXPLODIR = "Explodir "
fun validaTabuleiro(numLinhas: Int, numColunas: Int): Boolean {
    when {
        (numLinhas == 5 && numColunas == 6) -> return true
        (numLinhas == 6 && numColunas == 7) -> return true
        (numLinhas == 7 && numColunas == 8) -> return true
        else -> {
            return false
        }
    }
}
fun leJogo(nomeDoFicheiro: String): Pair<String, Array<Array<String?>>> {
    val conteudo = File(nomeDoFicheiro).readLines()
    if (conteudo.isNotEmpty()) {
        val nomeJogador = conteudo[0]
        val numLinhas = conteudo.size - 1
        val tabuleiro = Array(numLinhas) { linha ->
            val valores = conteudo[linha + 1].split(",")
            val linhaArray = Array<String?>(valores.size) { null }
            for (i in valores.indices) {
                linhaArray[i] = when (valores[i]) {
                    SIMBOLO_HUMANO -> BALAO_VERMELHO
                    SIMBOLO_COMPUTADOR -> BALAO_AZUL
                    VAZIO, NULL_STRING -> null
                    else -> valores[i]
                }
            }
            linhaArray
        }
        return Pair(nomeJogador, tabuleiro)
    } else {
        return Pair(VAZIO, Array(0) { arrayOfNulls<String>(0) })
    }
}
fun gravaJogo(nomeDoFicheiro: String, tabuleiro: Array<Array<String?>>, nomeDoJogador: String) {
    val conteudo = StringBuilder()
    conteudo.append(nomeDoJogador).append("\n")
    for (linha in tabuleiro) {
        for ((index, valor) in linha.withIndex()) {
            if (valor == null) {
                conteudo.append(VAZIO)
            } else {
                conteudo.append(
                    when (valor) {
                        BALAO_VERMELHO -> SIMBOLO_HUMANO
                        BALAO_AZUL -> SIMBOLO_COMPUTADOR
                        else -> valor
                    }
                )
            }
            if (index != linha.size - 1) {
                conteudo.append(",")
            }
        }
        conteudo.append("\n")
    }
    File(nomeDoFicheiro).writeText(conteudo.toString())
}
fun jogadaExplodirComputador(tabuleiro: Array<Array<String?>>): Pair<Int, Int> {
    val numLinhas = tabuleiro.size
    val numColunas = tabuleiro[0].size
    for (linha in 0 until numLinhas) {
        var ultimaSequencia = Pair(-1, -1)
        for (coluna in 0 until numColunas - 2) {
            val bolinha = tabuleiro[linha][coluna]
            if (bolinha != null && bolinha == tabuleiro[linha][coluna + 1] && bolinha == tabuleiro[linha][coluna + 2]) {
                ultimaSequencia = Pair(linha, coluna)
            }
        }
        if (ultimaSequencia.first != -1) {
            return ultimaSequencia
        }
    }
    for (coluna in 0 until numColunas) {
        for (linha in 0 until numLinhas - 2) {
            val bolinha = tabuleiro[linha][coluna]
            if (bolinha != null && bolinha == tabuleiro[linha + 1][coluna] && bolinha == tabuleiro[linha + 2][coluna]) {
                return Pair(linha, coluna)
            }
        }
    }
    var maxBaloes = -1
    var colunaEscolhida = -1
    for (coluna in 0 until numColunas) {
        val baloesNaColuna = (0 until numLinhas).count { tabuleiro[it][coluna] != null }
        if (baloesNaColuna > maxBaloes || (baloesNaColuna == maxBaloes && coluna > colunaEscolhida)) {
            maxBaloes = baloesNaColuna
            colunaEscolhida = coluna
        }
    }
    if (colunaEscolhida != -1) {
        for (linha in 0 until numLinhas) {
            if (tabuleiro[linha][colunaEscolhida] != null) {
                return Pair(linha, colunaEscolhida)
            }
        }
    }
    return Pair(-1, -1)
}
fun explodeBalao(tabuleiro: Array<Array<String?>>, coordenadas: Pair<Int, Int>): Boolean {
    val (linhaEscolhida, colunaEscolhida) = coordenadas
    val numLinhas = tabuleiro.size
    val numColunas = if (tabuleiro.isNotEmpty()) tabuleiro[0].size else 0
    if (linhaEscolhida !in 0 until numLinhas || colunaEscolhida !in 0 until numColunas) {
        return false
    }
    if (tabuleiro[linhaEscolhida][colunaEscolhida] != null) {
        for (linha in linhaEscolhida until numLinhas - 1) {
            tabuleiro[linha][colunaEscolhida] = tabuleiro[linha + 1][colunaEscolhida]
        }
        tabuleiro[numLinhas - 1][colunaEscolhida] = null
        return true
    }
    return false
}
fun eEmpate(tabuleiro: Array<Array<String?>>): Boolean {
    if (tabuleiro.size > tabuleiro[0].size) {
        return true
    }
    var linha2 = 0
    var contador = 0
    var  coluna = 0
    while (linha2 in 0 until tabuleiro.size){
        while (coluna in 0 until tabuleiro[linha2].size){
            if (tabuleiro[linha2][coluna] == BALAO_AZUL){
                contador ++
            }
            coluna++
        }
        coluna = 0
        linha2 ++
        if (contador > 5){
            return true
        }
    }
        for (linha in tabuleiro) {
            for (celula in linha) {
                if (celula == null) return false
            }
        }

    return true
}
fun ganhouJogo(tabuleiro: Array<Array<String?>>): Boolean {
    if (eVitoriaDiagonal(tabuleiro) || eVitoriaVertical(tabuleiro) || eVitoriaHorizontal(tabuleiro)) {
        return true
    } else return false
}
fun eVitoriaDiagonal(tabuleiro: Array<Array<String?>>): Boolean {
    val numLinhas = tabuleiro.size
    val numColunas = tabuleiro[0].size
    for (linha in 0 until numLinhas) {
        for (coluna in 0 until numColunas) {
            val atual = tabuleiro[linha][coluna]
            if (atual != null) {
                if (linha + 3 < numLinhas && coluna + 3 < numColunas &&
                    atual == tabuleiro[linha + 1][coluna + 1] &&
                    atual == tabuleiro[linha + 2][coluna + 2] &&
                    atual == tabuleiro[linha + 3][coluna + 3]
                ) return true
                if (linha - 3 >= 0 && coluna + 3 < numColunas &&
                    atual == tabuleiro[linha - 1][coluna + 1] &&
                    atual == tabuleiro[linha - 2][coluna + 2] &&
                    atual == tabuleiro[linha - 3][coluna + 3]
                ) return true
            }
        }
    }
    return false
}
fun eVitoriaVertical(tabuleiro: Array<Array<String?>>): Boolean {
    val numColunas = tabuleiro[0].size
    for (coluna in 0 until numColunas) {
        var contador = 0
        var ultimoValor: String? = null
        for (linha in tabuleiro) {
            if (linha[coluna] == ultimoValor && linha[coluna] != null) {
                contador++
                if (contador == 4) return true
            } else {
                ultimoValor = linha[coluna]
                contador = if (linha[coluna] != null) 1 else 0
            }
        }
    }
    return false
}
fun eVitoriaHorizontal(tabuleiro: Array<Array<String?>>): Boolean {
    for (linha in tabuleiro) {
        var contador = 0
        var ultimoValor: String? = null
        for (celula in linha) {
            if (celula == ultimoValor && celula != null) {
                contador++
                if (contador == 4) return true
            } else {
                ultimoValor = celula
                contador = if (celula != null) 1 else 0
            }
        }
    }
    return false
}
fun jogadaNormalComputador(tabuleiro: Array<Array<String?>>): Int {
    for (linha in tabuleiro.indices) {
        for (coluna in tabuleiro[linha].indices) {
            if (tabuleiro[linha][coluna] == null) {
                return coluna
            }
        }
    }
    return 2
}
fun colocaBalao(tabuleiro: Array<Array<String?>>, coluna: Int, humano: Boolean): Boolean {
    if (coluna == tabuleiro[0].size - 1) {
        if (coluna !in tabuleiro[0].indices) return false
        for (linha in tabuleiro.indices) {
            if (tabuleiro[linha][coluna] == null) {
                tabuleiro[linha][coluna] = if (humano) BALAO_VERMELHO else BALAO_AZUL
                return true
            }
        }
    }
    return false
}
fun contaBaloesColuna(tabuleiro: Array<Array<String?>>, coluna: Int): Int {
    if (coluna !in tabuleiro[0].indices) return 0
    var contador = 0
    for (linha in tabuleiro) {
        if (linha[coluna] != null) {
            contador++
        }
    }
    return contador
}
fun contaBaloesLinha(tabuleiro: Array<Array<String?>>, linha: Int): Int {
    if (linha !in tabuleiro.indices) return 0
    var contador = 0
    for (celula in tabuleiro[linha]) {
        if (celula != null) {
            contador++
        }
    }
    return contador
}
fun processaColuna(numColunas: Int, coluna: String?): Int? {
    if (coluna.isNullOrEmpty()) {
        return null
    }
    if (coluna.length == 1 && coluna[0] in 'A'..('A' + numColunas - 1)) {
        return coluna[0] - 'A'
    }
    return null
}
fun nomeValido(nome: String): Boolean {
    when {
        nome.length > 12 -> {
            return false
        }
        nome.length < 3 -> {
            return false
        }
        else -> {
            var contador1 = 0
            while (contador1 < nome.length) {
                if (nome[contador1] == ' ') {
                    return false
                }
                contador1++
            }
        }
    }
    return true
}
fun criaTabuleiro(tabuleiro: Array<Array<String?>>, mostrarLegenda: Boolean = true): String {
    val numColunas = tabuleiro[0].size
    val topo = criaTopoTabuleiro(numColunas)
    val linhasBuilder = StringBuilder()
    for (linha in tabuleiro) {
        linhasBuilder.append("\u2551")
        for ((index, celula) in linha.withIndex()) {
            when (celula) {
                null -> linhasBuilder.append("   ")
                BALAO_VERMELHO -> linhasBuilder.append(" $BALAO_VERMELHO ")
                BALAO_AZUL -> linhasBuilder.append(" $BALAO_AZUL ")
                else -> linhasBuilder.append("   ")
            }
            if (index < linha.size - 1) {
                linhasBuilder.append("|")
            }
        }
        linhasBuilder.append("\u2551\n")
    }
    val legenda = if (mostrarLegenda) "\n" + criaLegendaHorizontal(numColunas) else ""
    return "$topo\n${linhasBuilder.toString().trimEnd()}$legenda"
}
fun criaLegendaHorizontal(numColunas: Int): String {
    if (numColunas < 1) return ""
    val legendaBuilder = StringBuilder(" ")
    var letra = 'A'
    for (i in 0 until numColunas) {
        legendaBuilder.append(" $letra ")
        letra++
        if (i < numColunas - 1) {
            legendaBuilder.append("|")
        } else {
            legendaBuilder.append(" ")
        }
    }
    return legendaBuilder.toString()
}
fun criaTopoTabuleiro(numColunas: Int): String {
    val topoBuilder = StringBuilder("\u2554")
    val comprimento = 4 * numColunas - 1
    for (i in 0 until comprimento) {
        topoBuilder.append("\u2550")
    }
    topoBuilder.append("\u2557")
    return topoBuilder.toString()
}
fun criaTabuleiroVazio(numLinhas: Int, numColunas: Int): Array<Array<String?>> {
    return Array(numLinhas) { Array(numColunas) { null } }
}
fun main() {
    var running2 = true
    var carregou = 0
    var tabuleiro = ""
    var legenda = ""
    var nome = ""
    var jogou = 0
    var tabuleiroVazio: Array<Array<String?>> = arrayOf(arrayOfNulls(0))
    while (running2) {
        var jogo: Pair<String, Array<Array<String?>>> = Pair("", arrayOf(arrayOfNulls<String>(0)))
        if (jogou == 0) {
            println("\nBem-vindo ao jogo \"4 Baloes em Linha\"!\n\n1. Novo Jogo\n2. Gravar Jogo\n3. Ler Jogo\n0. Sair\n")
        } else println("\n1. Novo Jogo\n2. Gravar Jogo\n3. Ler Jogo\n0. Sair\n")
        var escolha = readln().toIntOrNull()
        while (escolha == null || (escolha != 0 && escolha != 1 && escolha != 2 && escolha != 3 && escolha != 9)) {
            println("Opcao invalida. Por favor, tente novamente.")
            escolha = readln().toIntOrNull()
        }
        if (escolha == 2) {
            if (jogou == 0) {
                println("Funcionalidade Gravar nao esta disponivel")
                escolha = readln().toIntOrNull()
                while (escolha == null || (escolha != 0 && escolha != 1 && escolha != 2 && escolha != 3 && escolha != 9)) {
                    println("Opcao invalida. Por favor, tente novamente.")
                    escolha = readln().toIntOrNull()
                }
            } else {
                println("Introduza o nome do ficheiro (ex: jogo.txt)")
                val ficheiro = readln()
                gravaJogo(ficheiro, tabuleiroVazio, nome)
                val linhaGrava = tabuleiroVazio.size
                val colunaGrava = tabuleiroVazio[0].size
                println("Tabuleiro $linhaGrava" + "x$colunaGrava gravado com sucesso")
                escolha = readln().toIntOrNull()
                while (escolha == null || (escolha != 0 && escolha != 1 && escolha != 2 && escolha != 3 && escolha != 9)) {
                    println("Opcao invalida. Por favor, tente novamente.")
                    escolha = readln().toIntOrNull()
                }
            }
        }
        if (escolha == 3) {
            println("Introduza o nome do ficheiro (ex: jogo.txt)")
            val ficheiro = readln()
            jogo = leJogo(ficheiro)
            carregou = 1
            val linhaGrava = jogo.second.size
            val colunaGrava = jogo.second[0].size
            println("Tabuleiro $linhaGrava" + "x$colunaGrava lido com sucesso!")
            escolha = 1
        }
        if (escolha == 0 || escolha == 9) {
            println("Prima enter para sair")
            readln()
            running2 = false
        }
        var numLinhas = 0
        var numColunas = 0
        var running = true
        if (escolha == 1) {
            jogou++
            do {
                if (carregou != 1) {
                    do {
                        do {
                            println("Numero de linhas:")
                            numLinhas = readln().toIntOrNull() ?: 0
                            if (numLinhas < 1)
                                println("Numero invalido")
                        } while (numLinhas < 1)
                        do {
                            println("Numero de colunas:")
                            numColunas = readln().toIntOrNull() ?: 0
                            if (numColunas < 1)
                                println("Numero invalido")
                        } while (numColunas < 1)
                        val validaTabuleiro = validaTabuleiro(numLinhas, numColunas)
                        if (!validaTabuleiro) {
                            println("Tamanho do tabuleiro invalido")
                        }
                    } while (!validaTabuleiro)
                    tabuleiroVazio = criaTabuleiroVazio(numLinhas, numColunas)
                    do {
                        println("Nome do jogador 1:")
                        nome = readln()
                        val validar = nomeValido(nome)
                        if (!validar) {
                            println("Nome de jogador invalido")
                        }
                    } while (!validar)
                    legenda = criaLegendaHorizontal(numColunas)
                    tabuleiro = criaTabuleiro(tabuleiroVazio, mostrarLegenda = true)
                } else {
                    nome = jogo.first
                    tabuleiroVazio = jogo.second
                    numLinhas = jogo.second.size
                    numColunas = jogo.second[0].size
                    legenda = if (numColunas > 0) criaLegendaHorizontal(numColunas) else ""
                }
                val primeiraLetra = if (numColunas > 0) 'A' else ' '
                val ultimaLetra = if (numColunas > 0) 'A' + (numColunas - 1) else ' '
                var jogador = 1
                var escolhaJogada = ""
                var running1 = true
                while (running1 && running) {
                    tabuleiro = criaTabuleiro(tabuleiroVazio, mostrarLegenda = true)
                    if (jogador % 2 == 0) {
                        println("$tabuleiro\n\nComputador: $BALAO_AZUL\nTabuleiro $numLinhas" + "X$numColunas")
                    } else println(
                              "$tabuleiro\n\n$nome: $BALAO_VERMELHO\nTabuleiro $numLinhas" + "X$numColunas\nColuna? " +
                              "($primeiraLetra..$ultimaLetra):")
                    if (jogador % 2 != 0) {
                        escolhaJogada = readln()
                        if (escolhaJogada == "Sair") {
                            running1 = false
                            running = false
                        } else if (escolhaJogada == COMANDO_GRAVAR) {
                            println("Introduza o nome do ficheiro (ex: jogo.txt)")
                            val ficheiro = readln()
                            val linhaGrava = tabuleiroVazio.size
                            val colunaGrava = tabuleiroVazio[0].size
                            println("Tabuleiro $linhaGrava" + "x$colunaGrava gravado com sucesso")
                            gravaJogo(ficheiro, tabuleiroVazio, nome)
                            running1 = false
                            running = false
                        } else if (escolhaJogada.startsWith(COMANDO_EXPLODIR)) {
                            val coluna = escolhaJogada[9]
                            if (coluna !in primeiraLetra..ultimaLetra) {
                                println("Coluna invalida")
                            } else {
                                var confirma = 'A'
                                var numeroExplode = 0
                                while (coluna != confirma) {
                                    confirma++
                                    numeroExplode++
                                }
                                val totalBaloes = tabuleiroVazio.sumOf { linha -> linha.count { it != null } }
                                if (totalBaloes < 2) {
                                    println("Funcionalidade Explodir nao esta disponivel")
                                } else {
                                    val coordenadas = Pair(0, numeroExplode)
                                    if (!explodeBalao(tabuleiroVazio, coordenadas)) {
                                        println("Coluna vazia")
                                    }
                                }
                            }
                        }
                    }
                    var jogada: Int? = null
                    if (running1 && running) {
                        jogada = processaColuna(numColunas, escolhaJogada)
                        while (jogada == null && running1) {
                            if (escolhaJogada == "Sair") {
                                running1 = false
                                running = false
                            } else {
                                println("Coluna invalida")
                                println("Coluna? ($primeiraLetra..$ultimaLetra):")
                                escolhaJogada = readln()
                                jogada = processaColuna(numColunas, escolhaJogada)
                            }
                        }
                        if (jogada != null) {
                            if (jogador % 2 == 0) {
                                val numeroPc = jogadaNormalComputador(tabuleiroVazio)
                                var jogadaPc = 'A'
                                jogadaPc += numeroPc
                                colocaBalao(tabuleiroVazio, numeroPc, false)
                                println("Coluna escolhida: $jogadaPc")
                            } else {
                                colocaBalao(tabuleiroVazio, jogada, true)
                                println("Coluna escolhida: $escolhaJogada")
                            }
                        }
                    }
                    if (ganhouJogo(tabuleiroVazio) && jogador % 2 == 0) {
                        tabuleiro = criaTabuleiro(tabuleiroVazio, mostrarLegenda = true)
                        println(tabuleiro)
                        println("\nPerdeu! Ganhou o Computador.")
                        running = false
                    } else if (ganhouJogo(tabuleiroVazio) && jogador % 2 != 0) {
                        tabuleiro = criaTabuleiro(tabuleiroVazio, mostrarLegenda = true)
                        println(tabuleiro)
                        println("\nParabens $nome! Ganhou!")
                        running = false
                    } else if (eEmpate(tabuleiroVazio)) {
                        tabuleiro = criaTabuleiro(tabuleiroVazio, mostrarLegenda = true)
                        println(tabuleiro)
                        println("Empate")
                        running = false
                    }
                    jogador++
                }
            } while (running)
        }
    }
    println("A sair...")
}