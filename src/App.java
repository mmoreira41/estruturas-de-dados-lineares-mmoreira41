import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Tarefas (App):
 * - Tarefa 1 (Pilha): finalizarPedido empilha o pedido finalizado (mais recente no topo).
 * - Tarefa 3 (Pilha): listarProdutosPedidosRecentes exibe itens dos pedidos mais novos.
 * - Tarefa 1 (Fila): Fila<Pedido> para registrar pedidos na ordem de finalização.
 * - Tarefa 2 (Fila): exibirValorMedioPrimeirosPedidos usa calcularValorMedio.
 * - Tarefa 3 (Fila): exibirPedidosAcimaDeValor e exibirPedidosComProduto usam filtrar.
 * - Tarefa 4 (App): opções 7-9 no menu, chamando os métodos acima.
 */
public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Pilha de pedidos (mais recentes no topo) */
    static Pilha<Pedido> pilhaPedidos = new Pilha<>();
    
    /** Fila de pedidos (ordem que foram finalizados) */
    static Fila<Pedido> filaPedidos = new Fila<>();
        
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("7 - Exibir valor total médio dos N primeiros pedidos");
        System.out.println("8 - Exibir primeiros pedidos com valor total acima de X");
        System.out.println("9 - Exibir primeiros pedidos que contêm um produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null 
     */
    static Produto localizarProduto() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
        int idProduto = lerOpcao("Digite o código identificador do produto desejado: ", Integer.class);
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].hashCode() == idProduto) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        return produto;   
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto localizarProdutoDescricao() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	String descricao;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.equals(descricao)) {
        		produto = produtosCadastrados[i];
        		localizado = true;
    		}
        }
        
        return produto;
    }
    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
    	
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }
    
    /** 
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {
    	
    	int formaPagamento = lerOpcao("Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo", Integer.class);
    	Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
    	Produto produto;
    	int numProdutos;
    	
    	listarTodosOsProdutos();
    	System.out.println("Incluindo produtos no pedido...");
    	numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
        	produto = localizarProdutoDescricao();
        	if (produto == null) {
        		System.out.println("Produto não encontrado");
        		i--;
        	} else {
        		pedido.incluirProduto(produto);
        	}
        }
    	
    	return pedido;
    }
    
    /**
     * Tarefa 1 (Pilha/Fila): Ao finalizar, armazena em pilha (recentes) e fila (ordem).
     */
    public static void finalizarPedido(Pedido pedido) {
    	
    	if (pedido == null) {
    		System.out.println("Não há pedido em aberto para finalizar.");
    		return;
    	}
    	
    	// Empilha o pedido finalizado na pilha de pedidos recentes
    	pilhaPedidos.empilhar(pedido);
    	// Enfileira o pedido finalizado para consultas em ordem de chegada
    	filaPedidos.inserir(pedido);
    	System.out.println("Pedido finalizado e armazenado com sucesso.\n");
    	System.out.println(pedido.toString());
    }
    
    /**
     * Tarefa 3 (Pilha): Lista produtos dos pedidos mais recentes sem alterar a pilha original.
     */
    public static void listarProdutosPedidosRecentes() {
    	
    	if (pilhaPedidos.vazia()) {
    		cabecalho();
    		System.out.println("Não há pedidos finalizados.");
    		return;
    	}
    	
    	Integer quantos = lerOpcao("Quantos pedidos recentes deseja listar?", Integer.class);
    	if (quantos == null || quantos <= 0) {
    		System.out.println("Quantidade inválida.");
    		return;
    	}
    	
    	Pilha<Pedido> ultimos;
    	try {
    		ultimos = pilhaPedidos.subPilha(quantos);
    	} catch (IllegalArgumentException e) {
    		// Se a quantidade solicitada for maior que o tamanho da pilha, ajusta para o máximo disponível
    		// Uma maneira simples é iterar desempilhando no máximo 'quantos' ou até esvaziar
    		ultimos = new Pilha<>();
    		Pilha<Pedido> buffer = new Pilha<>();
    		int copiados = 0;
    		while (!pilhaPedidos.vazia() && copiados < quantos) {
    			Pedido p = pilhaPedidos.desempilhar();
    			buffer.empilhar(p);
    			copiados++;
    		}
    		while (!buffer.vazia()) {
    			Pedido p = buffer.desempilhar();
    			ultimos.empilhar(p);
    			pilhaPedidos.empilhar(p);
    		}
    	}
    	
    	cabecalho();
    	System.out.println("Produtos dos pedidos mais recentes:\n");
    	
    	// Percorre a subpilha sem alterar a pilha original
    	Pilha<Pedido> leitura = new Pilha<>();
    	while (!ultimos.vazia()) {
    		Pedido p = ultimos.desempilhar();
    		leitura.empilhar(p);
    		System.out.println(String.format("Pedido %02d de %s", p.getIdPedido(), p.getDataPedido().toString()));
    		Produto[] prods = p.getProdutos();
    		for (int i = 0; i < p.getQuantosProdutos(); i++) {
    			System.out.println(prods[i].toString());
    		}
    		System.out.println();
    	}
    	while (!leitura.vazia()) {
    		ultimos.empilhar(leitura.desempilhar());
    	}
    }

	/**
	 * Tarefa 2 (Fila): Calcula e exibe a média do valor total de N primeiros pedidos.
	 */
	public static void exibirValorMedioPrimeirosPedidos() {
		if (filaPedidos.vazia()) {
			cabecalho();
			System.out.println("Não há pedidos finalizados.");
			return;
		}
		Integer n = lerOpcao("Calcular média dos primeiros quantos pedidos?", Integer.class);
		if (n == null || n <= 0) {
			System.out.println("Quantidade inválida.");
			return;
		}
		Function<Pedido, Double> extrator = p -> p.valorFinal();
		double media = filaPedidos.calcularValorMedio(extrator, n);
		cabecalho();
		System.out.println(String.format("Valor total médio dos %d primeiros pedidos: R$ %.2f", n, media));
	}

	/**
	 * Tarefa 3 (Fila): Filtra N primeiros pedidos cujo total é acima de um limite.
	 */
	public static void exibirPedidosAcimaDeValor() {
		if (filaPedidos.vazia()) {
			cabecalho();
			System.out.println("Não há pedidos finalizados.");
			return;
		}
		Integer n = lerOpcao("Considerar os primeiros quantos pedidos?", Integer.class);
		if (n == null || n <= 0) {
			System.out.println("Quantidade inválida.");
			return;
		}
		System.out.println("Informe o valor mínimo (use ponto ou vírgula):");
		String linha = teclado.nextLine().replace(",", ".");
		double limite;
		try {
			limite = Double.parseDouble(linha);
		} catch (NumberFormatException e) {
			System.out.println("Valor inválido.");
			return;
		}
		Predicate<Pedido> cond = p -> p.valorFinal() > limite;
		Fila<Pedido> filtrados = filaPedidos.filtrar(cond, n);
		cabecalho();
		System.out.println(String.format("Pedidos com valor acima de R$ %.2f (dentre os %d primeiros):\n", limite, n));
		int count = 0;
		while (!filtrados.vazia()) {
			Pedido p = filtrados.remover();
			System.out.println(p.toString());
			System.out.println();
			count++;
		}
		if (count == 0) {
			System.out.println("Nenhum pedido encontrado.");
		}
	}

	/**
	 * Tarefa 3 (Fila): Filtra N primeiros pedidos que contêm um produto informado.
	 */
	public static void exibirPedidosComProduto() {
		if (filaPedidos.vazia()) {
			cabecalho();
			System.out.println("Não há pedidos finalizados.");
			return;
		}
		Integer n = lerOpcao("Considerar os primeiros quantos pedidos?", Integer.class);
		if (n == null || n <= 0) {
			System.out.println("Quantidade inválida.");
			return;
		}
		System.out.println("Digite o nome/descrição do produto a procurar:");
		String alvo = teclado.nextLine();
		Predicate<Pedido> cond = p -> {
			Produto[] prods = p.getProdutos();
			for (int i = 0; i < p.getQuantosProdutos(); i++) {
				Produto prod = prods[i];
				if (prod.descricao.equalsIgnoreCase(alvo)) {
					return true;
				}
			}
			return false;
		};
		Fila<Pedido> filtrados = filaPedidos.filtrar(cond, n);
		cabecalho();
		System.out.println(String.format("Pedidos que contêm o produto '%s' (dentre os %d primeiros):\n", alvo, n));
		int count = 0;
		while (!filtrados.vazia()) {
			Pedido p = filtrados.remover();
			System.out.println(p.toString());
			System.out.println();
			count++;
		}
		if (count == 0) {
			System.out.println("Nenhum pedido encontrado.");
		}
	}
    
	public static void main(String[] args) {
		
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
		nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        
        Pedido pedido = null;
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> finalizarPedido(pedido);
                case 6 -> listarProdutosPedidosRecentes();
                case 7 -> exibirValorMedioPrimeirosPedidos();
                case 8 -> exibirPedidosAcimaDeValor();
                case 9 -> exibirPedidosComProduto();
            }
            pausa();
        }while(opcao != 0);       

        teclado.close();    
    }
}
