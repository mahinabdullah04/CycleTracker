package ca.umanitoba.cs.abdullmm.model;

/**
 * A linked list implementation of the Stack interface.
 *
 * Class Invariants:
 * - The stack maintains LIFO ordering through the linked node structure
 * - The top pointer references the most recently added node (or null if empty)
 * - Each node (except top) has a next node pointing to its predecessor
 * - size >= 0 and accurately reflects the number of elements
 *
 * @param <T> the type of elements in the stack
 */
public class LinkedStack<T> implements Stack<T> {
    private Node<T> top;
    private int size;

    /**
     * Inner class representing a node in the linked stack.
     *
     * @param <T> the type of element stored in the node
     */
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    /**
     * Constructs an empty LinkedStack.
     *
     * Postcondition: the stack is empty with size 0
     */
    public LinkedStack() {
        this.top = null;
        this.size = 0;
        checkInvariant();
    }

    /**
     * Pushes an element onto the stack.
     *
     * Postcondition: element is now the top of the stack and size has increased by 1
     *
     * @param item the item to push
     */
    @Override
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
        size++;
        checkInvariant();
    }

    /**
     * Pops and returns the top element from the stack.
     *
     * Postcondition: the former top element has been removed and size has decreased by 1
     *
     * @return the top element
     * @throws Stack.EmptyStackException if the stack is empty
     */
    @Override
    public T pop() throws Stack.EmptyStackException {
        if (isEmpty()) {
            throw new Stack.EmptyStackException("Cannot pop from an empty stack");
        }
        T data = top.data;
        top = top.next;
        size--;
        checkInvariant();
        return data;
    }

    /**
     * Returns the number of items on the stack.
     *
     * @return the number of items on the stack
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Checks if the stack is empty.
     *
     * @return true if the stack is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Peeks at the top element without removing it.
     *
     * @return the top element
     * @throws Stack.EmptyStackException if the stack is empty
     */
    @Override
    public T peek() throws Stack.EmptyStackException {
        if (isEmpty()) {
            throw new Stack.EmptyStackException("Cannot peek at an empty stack");
        }
        return top.data;
    }

    /**
     * Verifies the class invariants.
     *
     * Invariant assertions:
     * - size >= 0
     * - if size == 0, then top == null
     * - if size > 0, then top != null
     */
    private void checkInvariant() {
        assert size >= 0 : "Stack size cannot be negative";
        assert (size == 0 && top == null) || (size > 0 && top != null) :
                "Stack invariant violated: size and top pointer are inconsistent";
    }
}
