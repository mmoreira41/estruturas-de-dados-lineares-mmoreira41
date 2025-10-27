import java.util.NoSuchElementException;

public class Pilha<E> {

	private Celula<E> topo;
	private Celula<E> fundo;

	public Pilha() {

		Celula<E> sentinela = new Celula<E>();
		fundo = sentinela;
		topo = sentinela;

	}

	public boolean vazia() {
		return fundo == topo;
	}

	public void empilhar(E item) {

		topo = new Celula<E>(item, topo);
	}

	public E desempilhar() {

		E desempilhado = consultarTopo();
		topo = topo.getProximo();
		return desempilhado;

	}

	public E consultarTopo() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha!");
		}

		return topo.getItem();

	}

	/**
	 * Tarefa 2 (Pilha): subPilha
	 * Cria e devolve uma nova pilha com os 'numItens' elementos do topo,
	 * preservando a ordem. Lança exceção se não houver itens suficientes.
	 */
	public Pilha<E> subPilha(int numItens) {
		
		if (numItens < 0) {
			throw new IllegalArgumentException("O número de itens não pode ser negativo.");
		}

		// Verifica se há itens suficientes na pilha
		int contador = 0;
		Celula<E> cursor = topo;
		while (cursor != fundo && contador < numItens) {
			contador++;
			cursor = cursor.getProximo();
		}
		if (contador < numItens) {
			throw new IllegalArgumentException("A pilha não contém elementos suficientes.");
		}

		Pilha<E> nova = new Pilha<>();

		// Copia os primeiros numItens itens preservando a ordem (topo permanece topo)
		Object[] buffer = new Object[numItens];
		cursor = topo;
		for (int i = 0; i < numItens; i++) {
			buffer[i] = cursor.getItem();
			cursor = cursor.getProximo();
		}
		for (int i = numItens - 1; i >= 0; i--) {
			@SuppressWarnings("unchecked")
			E item = (E) buffer[i];
			nova.empilhar(item);
		}

		return nova;
	}
}