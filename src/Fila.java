import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Tarefa 1 (Fila): Implementa uma fila genérica encadeada com sentinela.
 * - inserir(E): adiciona no final (ordem de chegada preservada).
 * - remover(): retira do início; lança exceção se vazia.
 * Esta estrutura é usada para percorrer pedidos na ordem que foram finalizados.
 */
public class Fila<E> {

	private Celula<E> frente;
	private Celula<E> tras;

	public Fila() {
		Celula<E> sentinela = new Celula<E>();
		frente = sentinela;
		tras = sentinela;
	}

	public boolean vazia() {
		return frente == tras;
	}

	public void inserir(E item) {
		Celula<E> nova = new Celula<E>(item, null);
		tras.setProximo(nova);
		tras = nova;
	}

	public E remover() {
		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na fila!");
		}
		Celula<E> primeira = frente.getProximo();
		E item = primeira.getItem();
		frente.setProximo(primeira.getProximo());
		if (primeira == tras) {
			tras = frente;
		}
		return item;
	}

	/**
	 * Tarefa 2 (Fila): calcularValorMedio
	 * Calcula a média de um atributo dos primeiros 'quantidade' elementos,
	 * usando a função extratora passada por parâmetro.
	 * Se houver menos elementos, usa os que existirem; retorna 0.0 se nenhum.
	 */
	public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
		if (extrator == null) {
			throw new IllegalArgumentException("A função extratora não pode ser nula.");
		}
		if (quantidade <= 0) {
			return 0.0;
		}
		double soma = 0.0;
		int cont = 0;
		Celula<E> cursor = frente.getProximo();
		while (cursor != null && cont < quantidade) {
			Double valor = extrator.apply(cursor.getItem());
			soma += (valor == null ? 0.0 : valor.doubleValue());
			cont++;
			cursor = cursor.getProximo();
		}
		return cont == 0 ? 0.0 : (soma / cont);
	}

	/**
	 * Tarefa 3 (Fila): filtrar
	 * Cria uma nova fila com os elementos, dentre os primeiros 'quantidade',
	 * que satisfazem a condição do Predicate. Mantém a ordem original.
	 */
	public Fila<E> filtrar(Predicate<E> condicional, int quantidade) {
		if (condicional == null) {
			throw new IllegalArgumentException("A condição de filtro não pode ser nula.");
		}
		Fila<E> resultado = new Fila<E>();
		if (quantidade <= 0) {
			return resultado;
		}
		int cont = 0;
		Celula<E> cursor = frente.getProximo();
		while (cursor != null && cont < quantidade) {
			E item = cursor.getItem();
			if (condicional.test(item)) {
				resultado.inserir(item);
			}
			cont++;
			cursor = cursor.getProximo();
		}
		return resultado;
	}
}


