package Investimento;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

enum TipoCarteira {
    ACOES,
    FIIS
}

class Investidor {
    private String nome;
    private String usuario;
    private String senha;
    private List<CarteiraAcoes> carteirasAcoes;
    private List<CarteiraFIIs> carteirasFIIs;
    private double saldo; // Saldo do investidor
    private double valorEmAtivos; // Valor total em ativos
    private List<Double> historicoCompras;
    private List<Double> historicoVendas;

    public Investidor(String nome, String usuario, String senha, double saldoInicial) {
        this.nome = nome;
        this.usuario = usuario;
        this.senha = senha;
        this.carteirasAcoes = new ArrayList<>();
        this.carteirasFIIs = new ArrayList<>();
        this.saldo = saldoInicial;
        this.valorEmAtivos = 0.0; // Inicialmente, não há ativos na carteira
        this.historicoCompras = new ArrayList<>();
        this.historicoVendas = new ArrayList<>();
    }

    public void adicionarCarteiraAcoes(CarteiraAcoes carteira) {
        carteirasAcoes.add(carteira);
    }

    public void adicionarCarteiraFIIs(CarteiraFIIs carteira) {
        carteirasFIIs.add(carteira);
    }

    public String getNome() {
        return nome;
    }

    public List<CarteiraAcoes> getCarteirasAcoes() {
        return carteirasAcoes;
    }

    public List<CarteiraFIIs> getCarteirasFIIs() {
        return carteirasFIIs;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getSaldoFormatado() {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(saldo);
    }

    public double getValorEmAtivos() {
        return valorEmAtivos;
    }

    public boolean possuiSaldoSuficiente(double valor) {
        return saldo >= valor;
    }

    public void debitarSaldo(double valor) {
        saldo -= valor;
    }

    public void atualizarValorEmAtivos() {
        valorEmAtivos = 0.0;
        for (CarteiraAcoes carteira : carteirasAcoes) {
            for (Ativo ativo : carteira.getAtivos()) {
                valorEmAtivos += ativo.getPrecoAtual();
            }
        }
        for (CarteiraFIIs carteira : carteirasFIIs) {
            for (Ativo ativo : carteira.getAtivos()) {
                valorEmAtivos += ativo.getPrecoAtual();
            }
        }
    }

    public void mostrarCarteira(boolean crescente) {
        DecimalFormat df = new DecimalFormat("#.###"); // Formatação para até três casas decimais
        System.out.println("Carteira de " + nome + ":");
        System.out.println("Saldo disponível: " + df.format(saldo)); // Formatando o saldo

        if (!carteirasAcoes.isEmpty()) {
            System.out.println("Carteiras de Ações:");
            for (CarteiraAcoes carteira : carteirasAcoes) {
                System.out.println("Carteira: " + carteira.getNome());
                List<Ativo> ativosCarteira = new ArrayList<>(carteira.getAtivos());
                if (crescente) {
                    Collections.sort(ativosCarteira, Comparator.comparingDouble(Ativo::getPrecoAtual));
                } else {
                    Collections.sort(ativosCarteira, Comparator.comparingDouble(Ativo::getPrecoAtual).reversed());
                }
                System.out.println("Ativos:");
                for (Ativo ativo : ativosCarteira) {
                    double precoFormatado = Math.round(ativo.getPrecoAtual() * 1000.0) / 1000.0; // Arredonda para 3 casas decimais
                    System.out.println(ativo.getNome() + " (" + ativo.getSimbolo() + ") - Preço: " + df.format(precoFormatado));
                }
            }
        }

        if (!carteirasFIIs.isEmpty()) {
            System.out.println("Carteiras de FIIs:");
            for (CarteiraFIIs carteira : carteirasFIIs) {
                System.out.println("Carteira: " + carteira.getNome());
                List<Ativo> ativosCarteira = new ArrayList<>(carteira.getAtivos());
                if (crescente) {
                    Collections.sort(ativosCarteira, Comparator.comparingDouble(Ativo::getPrecoAtual));
                } else {
                    Collections.sort(ativosCarteira, Comparator.comparingDouble(Ativo::getPrecoAtual).reversed());
                }
                System.out.println("Ativos:");
                for (Ativo ativo : ativosCarteira) {
                    double precoFormatado = Math.round(ativo.getPrecoAtual() * 1000.0) / 1000.0; // Arredonda para 3 casas decimais
                    System.out.println(ativo.getNome() + " (" + ativo.getSimbolo() + ") - Preço: " + df.format(precoFormatado));
                }
            }
        }
    }
public void venderAtivo(Carteira carteira, String simbolo, double quantidade, TipoCarteira tipoCarteira, Corretora corretora, Investidor investidor) {
    Ativo ativoVenda = pesquisarAtivo(carteira, simbolo);
    if (ativoVenda != null) {
        double precoVenda = ativoVenda.getPrecoAtual() * quantidade;
        if (quantidade > 0 && carteira.getAtivos().contains(ativoVenda)) {
            if ((tipoCarteira == TipoCarteira.ACOES && ativoVenda instanceof Acao) || (tipoCarteira == TipoCarteira.FIIS && ativoVenda instanceof FII)) {
                // Aplicar taxa de venda da corretora
                double taxaCorretora = corretora.getTaxaCompra() / 100;
                precoVenda *= (1 - taxaCorretora);
                historicoVendas.add(precoVenda); // Adicionar ao histórico de vendas
                carteira.removerAtivo(ativoVenda, quantidade);
                investidor.creditarSaldo(precoVenda);
                System.out.println(quantidade + " ativos de " + ativoVenda.getNome() + " vendidos com sucesso.");
                System.out.println("Valor total da venda (com taxa): " + precoVenda);
                investidor.getHistoricoVendas().add(precoVenda); // Adicione isso após a venda bem-sucedida

            } else {
                System.out.println("Tipo de ativo não corresponde ao tipo de carteira selecionado.");
            }
        } else {
            System.out.println("Ativo não encontrado na carteira ou quantidade inválida.");
        }
    } else {
        System.out.println("Ativo não encontrado.");
    }
}


    

    public void creditarSaldo(double valor) {
        saldo += valor;
    }

    public Ativo pesquisarAtivo(Carteira carteira, String simbolo) {
        for (Ativo ativo : carteira.getAtivos()) {
            if (ativo.getSimbolo().equals(simbolo)) {
                return ativo;
            }
        }
        return null;
    }

    public List<Double> getHistoricoCompras() {
        return historicoCompras;
    }

    public List<Double> getHistoricoVendas() {
        return historicoVendas;
    }
}

// Resto do código permanece igual...


class Carteira {
    private String nome;
    private Stack<Ativo> ativos;

    public Carteira(String nome) {
        this.nome = nome;
        this.ativos = new Stack<>();
    }

    public void adicionarAtivo(Ativo ativo) {
        ativos.push(ativo);
    }

    public void removerAtivo(Ativo ativo, double quantidade) {
        for (int i = 0; i < quantidade; i++) {
            ativos.remove(ativo);
        }
    }

    public String getNome() {
        return nome;
    }

    public Stack<Ativo> getAtivos() {
        return ativos;
    }

    public Ativo pesquisarAtivo(String simbolo) {
        for (Ativo ativo : ativos) {
            if (ativo.getSimbolo().equals(simbolo)) {
                return ativo;
            }
        }
        return null;
    }
}

class CarteiraAcoes extends Carteira {
    public CarteiraAcoes(String nome) {
        super(nome);
    }
}

class CarteiraFIIs extends Carteira {
    public CarteiraFIIs(String nome) {
        super(nome);
    }
}

class Acao extends Ativo {
    public Acao(String nome, String simbolo, double precoCompra) {
        super(nome, simbolo, precoCompra);
    }
}

class FII extends Ativo {
    public FII(String nome, String simbolo, double precoCompra) {
        super(nome, simbolo, precoCompra);
    }
}

class Ativo {
    private String nome;
    private String simbolo;
    private double precoCompra;
    private double precoAtual;
    private List<Double> historicoPrecos;

    public Ativo(String nome, String simbolo, double precoCompra) {
        this.nome = nome;
        this.simbolo = simbolo;
        this.precoCompra = precoCompra;
        this.precoAtual = precoCompra;
        this.historicoPrecos = new ArrayList<>();
        historicoPrecos.add(precoCompra);
    }

    public List<Double> getHistoricoPrecos() {
        return historicoPrecos;
    }

    public String getNome() {
        return nome;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public double getPrecoCompra() {
        return precoCompra;
    }

    public double getPrecoAtual() {
        return precoAtual;
    }

    public void atualizarPreco(double novoPreco) {
        historicoPrecos.add(precoAtual);
        precoAtual = novoPreco;
    }

    @Override
    public String toString() {
        return nome + " (" + simbolo + ") - Preço: " + precoAtual;
    }
}

class Mercado {
    public List<Ativo> ativosDisponiveis;
    private Investidor investidor;

    public Mercado(Investidor investidor) {
        this.investidor = investidor;
        this.ativosDisponiveis = new ArrayList<>();
        
        Acao acaoPetrobras = new Acao("Ação Petrobras", "PETR4", 25.0);
        Acao acaoVale = new Acao("Ação Vale", "VALE3", 50.0);
        Acao acaoAmazon = new Acao("Ação Amazon", "AMZN", 3000.0);
        FII fiiXP = new FII("FII XP", "XPML11", 100.0);
        FII fiiHGLG = new FII("FII HGLG", "HGLG11", 80.0);
        
        // Adicionar ações à lista de ativos disponíveis
        ativosDisponiveis.add(acaoPetrobras);
        ativosDisponiveis.add(acaoVale);
        ativosDisponiveis.add(acaoAmazon);
        
        // Adicionar fundos imobiliários à lista de ativos disponíveis
        ativosDisponiveis.add(fiiXP);
        ativosDisponiveis.add(fiiHGLG);
    }

    public Ativo pesquisarAtivo(String simbolo) {
        for (Ativo ativo : ativosDisponiveis) {
            if (ativo.getSimbolo().equals(simbolo)) {
                return ativo;
            }
        }
        return null;
    }

    public boolean comprarAtivo(Carteira carteira, String simbolo, TipoCarteira tipoCarteira, Corretora corretora) {
        Ativo ativoEncontrado = pesquisarAtivo(simbolo);
        if (ativoEncontrado != null) {
            if ((tipoCarteira == TipoCarteira.ACOES && ativoEncontrado instanceof Acao) || (tipoCarteira == TipoCarteira.FIIS && ativoEncontrado instanceof FII)) {
                double precoCompra = ativoEncontrado.getPrecoCompra();
                if (carteira != null) {
                    if (investidor.possuiSaldoSuficiente(precoCompra)) {
                        // Aplicar taxa de compra da corretora
                        precoCompra *= (1 + corretora.getTaxaCompra() / 100);
                        carteira.adicionarAtivo(ativoEncontrado);
                        investidor.debitarSaldo(precoCompra);
                        System.out.println("Ativo comprado com sucesso: " + ativoEncontrado.getNome());
                        System.out.println("Preço de compra (com taxa): " + precoCompra);
                        investidor.getHistoricoCompras().add(precoCompra);
                        return true;
                    } else {
                        System.out.println("Não há saldo suficiente para comprar o ativo.");
                    }
                }
            } else {
                System.out.println("Tipo de ativo não corresponde ao tipo de carteira selecionado.");
            }
        }
        return false;
    }

    public void mostrarAtivos(boolean crescente) {
        List<Ativo> ativosOrdenados = new ArrayList<>(ativosDisponiveis);
        if (crescente) {
            Collections.sort(ativosOrdenados, Comparator.comparingDouble(Ativo::getPrecoAtual));
        } else {
            Collections.sort(ativosOrdenados, Comparator.comparingDouble(Ativo::getPrecoAtual).reversed());
        }

        DecimalFormat df = new DecimalFormat("#.###"); // Formatação para até três casas decimais

        System.out.println("Ativos disponíveis no momento:");
        for (Ativo ativo : ativosOrdenados) {
            double precoAtual = ativo.getPrecoAtual();
            List<Double> historicoPrecos = ativo.getHistoricoPrecos();
            
            if (historicoPrecos.size() >= 2) { // Verifica se há pelo menos dois preços no histórico
                double precoAnterior = historicoPrecos.get(historicoPrecos.size() - 2); // Pegar o preço anterior
                double variacaoPercentual = ((precoAtual - precoAnterior) / precoAnterior) * 100;

                // Verifica se a variação é positiva ou negativa
                String sinal = (variacaoPercentual >= 0) ? "+" : "";

                System.out.println(ativo);
                System.out.println("Variação percentual: " + sinal + df.format(variacaoPercentual) + "%");
            } else {
                System.out.println(ativo);
                System.out.println("Variação percentual: N/A (não há histórico suficiente)");
            }
        }
    }

    public void atualizarPrecos() {
        Random rand = new Random();
        for (Ativo ativo : ativosDisponiveis) {
            double variacao = rand.nextDouble() * 10 - 5;
            double novoPreco = ativo.getPrecoAtual() + variacao;
            ativo.atualizarPreco(novoPreco);
        }
        investidor.atualizarValorEmAtivos();
    }
}

class Corretora {
    private String nome;
    private double taxaCompra;

    public Corretora(String nome, double taxaCompra) {
        this.nome = nome;
        this.taxaCompra = taxaCompra;
    }

    public String getNome() {
        return nome;
    }

    public double getTaxaCompra() {
        return taxaCompra;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Investidor investidor = null;
        Mercado mercado = null;

        // Exibindo a mensagem de boas-vindas centralizada
        System.out.println("+----------------------------------+");
        System.out.println("| SEJA BEM VINDO A BOLSA DE VALORES |");
        System.out.println("+----------------------------------+");

        // Solicitando login e senha
        System.out.print("Usuário: ");
        String usuario = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        // Verificando se o usuário e senha correspondem a um investidor registrado
        // Neste exemplo, vamos considerar um investidor fixo apenas para fins de demonstração
        if (usuario.equals("joao") && senha.equals("senha123")) {
            investidor = new Investidor("João", "joao", "senha123", 1000.0);
            CarteiraAcoes carteiraAcoes = new CarteiraAcoes("Carteira Ações");
            CarteiraFIIs carteiraFIIs = new CarteiraFIIs("Carteira FIIs");

            investidor.adicionarCarteiraAcoes(carteiraAcoes);
            investidor.adicionarCarteiraFIIs(carteiraFIIs);

            mercado = new Mercado(investidor);
        } else {
            System.out.println("Usuário ou senha incorretos. Encerrando o programa.");
            scanner.close();
            return;
        }

        boolean continuar = true;
        while (continuar) {
            // Exibindo opções para o usuário
            System.out.println("\nOpções:");
            System.out.println("1 - Pesquisar Ativo");
            System.out.println("2 - Comprar Ativo");
            System.out.println("3 - Ver Saldo");
            System.out.println("4 - Carteira");
            System.out.println("5 - Mostrar Ativos e Preços no Momento");
            System.out.println("6 - Atualizar Preços");
            System.out.println("7 - Vender Ativo");
            System.out.println("8 - historico de vendas e compras");
            System.out.println("9 - parar");
        
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha após a leitura da opção
        
            switch (opcao) {
                case 1:
                    // Pesquisar ativo por símbolo
                    System.out.print("Digite o símbolo do ativo que deseja pesquisar: ");
                    String simboloPesquisa = scanner.next();
                    Ativo ativoPesquisado = mercado.pesquisarAtivo(simboloPesquisa);
                    if (ativoPesquisado != null) {
                        System.out.println("Ativo encontrado: " + ativoPesquisado.getNome());
                        System.out.println("Símbolo: " + ativoPesquisado.getSimbolo());
                        System.out.println("Preço atual: " + ativoPesquisado.getPrecoAtual());
                    } else {
                        System.out.println("Ativo não encontrado.");
                    }
                    break;
                    case 2:
                    // Comprar ativo
                    Corretora corretoraCompra = null; // Declare a variável corretora aqui, apenas uma vez
                
                    boolean corretoraValida = false;
                
                    while (!corretoraValida) {
                        System.out.print("Digite a corretora (Corretora X ou Corretora Y): ");
                        String nomeCorretora = scanner.nextLine();
                
                        if (nomeCorretora.equalsIgnoreCase("Corretora X")) {
                            corretoraCompra = new Corretora("Corretora X", 0.5); // Taxa de 0.5%
                            corretoraValida = true;
                        } else if (nomeCorretora.equalsIgnoreCase("Corretora Y")) {
                            corretoraCompra = new Corretora("Corretora Y", 0.3); // Taxa de 0.3%
                            corretoraValida = true;
                        } else {
                            System.out.println("Corretora não encontrada. Por favor, digite novamente.");
                        }
                    }
                
                    // Continuar com o código para comprar o ativo usando a corretoraCompra
                    System.out.print("Digite o símbolo do ativo que deseja comprar: ");
                    String simboloCompra = scanner.next();
                    System.out.print("Digite o tipo da carteira (ACOES ou FIIS): ");
                    String tipoCarteiraCompra = scanner.next();
                    Carteira carteiraCompra = selecionarCarteira(investidor, scanner, tipoCarteiraCompra);
                
                    if (carteiraCompra != null) {
                        mercado.comprarAtivo(carteiraCompra, simboloCompra, TipoCarteira.valueOf(tipoCarteiraCompra), corretoraCompra);
                    }
                    break;
                
                
                case 3:
                    // Ver saldo do investidor
                    System.out.println("Saldo atual: " + investidor.getSaldoFormatado());
                    break;
                case 4:
                    // Carteira
                    System.out.println("Escolha a ordem de exibição:");
                    System.out.println("1 - Crescente");
                    System.out.println("2 - Decrescente");
                    int subOpcao = scanner.nextInt();
                    boolean crescente = (subOpcao == 1);
                    investidor.mostrarCarteira(crescente);
                    break;
                case 5:
                    // Mostrar ativos e preços no momento
                    System.out.println("Escolha a ordem de exibição:");
                    System.out.println("1 - Crescente");
                    System.out.println("2 - Decrescente");
                    subOpcao = scanner.nextInt();
                    crescente = (subOpcao == 1);
                    mercado.mostrarAtivos(crescente);
                    break;
                case 6:
                    // Atualizar preços
                    mercado.atualizarPrecos();
                    System.out.println("Preços atualizados com sucesso.");
                    break;

                    
                 
                    case 7:
                    System.out.print("Digite o símbolo do ativo que deseja vender: ");
String simboloVenda = scanner.next();
System.out.print("Digite o tipo da carteira (ACOES ou FIIS): ");
String tipoCarteiraVenda = scanner.next();
Carteira carteiraVenda = selecionarCarteira(investidor, scanner, tipoCarteiraVenda);

if (carteiraVenda != null) {
    System.out.println("Digite a corretora (Corretora X ou Corretora Y): ");
    scanner.nextLine(); // Consuma a quebra de linha pendente

    String nomeCorretora = scanner.nextLine().trim(); // Use trim() para remover espaços em branco
    Corretora corretoraVenda = null;

    if (nomeCorretora.equalsIgnoreCase("Corretora X")) {
        corretoraVenda = new Corretora("Corretora X", 0.5); // Taxa de 0.5%
    } else if (nomeCorretora.equalsIgnoreCase("Corretora Y")) {
        corretoraVenda = new Corretora("Corretora Y", 0.3); // Taxa de 0.3%
    } else {
        System.out.println("Corretora não encontrada.");
        break;
    }

    try {
        System.out.print("Digite a quantidade de ativos que deseja vender: ");
        String quantidadeVendaStr = scanner.nextLine();
        double quantidadeVenda = Double.parseDouble(quantidadeVendaStr);

        if (quantidadeVenda > 0) {
            // Resto do código para vender o ativo
            investidor.venderAtivo(carteiraVenda, simboloVenda, quantidadeVenda, TipoCarteira.valueOf(tipoCarteiraVenda), corretoraVenda, investidor);
        } else {
            System.out.println("Quantidade inválida. Certifique-se de inserir um valor positivo para a quantidade.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Entrada inválida. Certifique-se de digitar um número válido para a quantidade.");
    }
}

                    break;
                
                

                case 9:
                    // Parar o programa
                    continuar = false;
                    System.out.println("Encerrando o programa...");
                    break;


                    case 8 :
    // Visualizar Histórico de Compras e Vendas
    // Visualizar Histórico de Compras e Vendas
    System.out.println("Histórico de Compras:");
    List<Double> historicoCompras = investidor.getHistoricoCompras();
    List<String> historicoComprasDetalhes = new ArrayList<>();

    for (Double compra : historicoCompras) {
        historicoComprasDetalhes.add("Compra de " + compra + " por " + investidor.getNome());
    }

    System.out.println(String.join("\n", historicoComprasDetalhes));

    System.out.println("\nHistórico de Vendas:");
    List<Double> historicoVendas = investidor.getHistoricoVendas();
    List<String> historicoVendasDetalhes = new ArrayList<>();

    for (Double venda : historicoVendas) {
        historicoVendasDetalhes.add("Venda de " + venda + " por " + investidor.getNome());
    }

    System.out.println(String.join("\n", historicoVendasDetalhes));
    break;


                default:
                    System.out.println("Opção inválida.");
            }
        }

        // Fechando o scanner
        scanner.close();
    }

    private static Carteira selecionarCarteira(Investidor investidor, Scanner scanner, String tipoCarteira) {
        if (tipoCarteira.equalsIgnoreCase("ACOES")) {
            List<CarteiraAcoes> carteirasAcoes = investidor.getCarteirasAcoes();
            if (carteirasAcoes.isEmpty()) {
                System.out.println("O investidor não possui carteiras de Ações.");
                return null;
            }
            System.out.println("Escolha uma carteira de Ações:");
            for (int i = 0; i < carteirasAcoes.size(); i++) {
                System.out.println((i + 1) + " - " + carteirasAcoes.get(i).getNome());
            }
            int escolha = scanner.nextInt();
            if (escolha >= 1 && escolha <= carteirasAcoes.size()) {
                return carteirasAcoes.get(escolha - 1);
            } else {
                System.out.println("Opção inválida.");
                return null;
            }
        } else if (tipoCarteira.equalsIgnoreCase("FIIS")) {
            List<CarteiraFIIs> carteirasFIIs = investidor.getCarteirasFIIs();
            if (carteirasFIIs.isEmpty()) {
                System.out.println("O investidor não possui carteiras de FIIs.");
                return null;
            }
            System.out.println("Escolha uma carteira de FIIs:");
            for (int i = 0; i < carteirasFIIs.size(); i++) {
                System.out.println((i + 1) + " - " + carteirasFIIs.get(i).getNome());
            }
            int escolha = scanner.nextInt();
            if (escolha >= 1 && escolha <= carteirasFIIs.size()) {
                return carteirasFIIs.get(escolha - 1);
            } else {
                System.out.println("Opção inválida.");
                return null;
            }
        } else {
            System.out.println("Tipo de carteira inválido.");
            return null;
        }
    }
}
