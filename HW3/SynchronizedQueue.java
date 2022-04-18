/**
 * A synchronized bounded-size queue for multithreaded producer-consumer applications.
 * 
 * @param <T> Type of data items
 */

import java.lang.Thread;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class SynchronizedQueue<T> {

	private T[] buffer;
//	private Queue<T> buffer;
	private int producers;
	private int capacity;
	private int front = 0;
	private int rear = capacity - 1;;
	private int size=0;
	// TODO: Add more private members here as necessary
	
	/**
	 * Constructor. Allocates a buffer (an array) with the given capacity and
	 * resets pointers and counters.
	 * @param capacity Buffer capacity
	 */
	@SuppressWarnings("unchecked")
	public SynchronizedQueue(int capacity) {
//		this.buffer = new LinkedList<>();
		this.buffer= (T[])(new Object[capacity]);
		this.producers = 0;
		this.capacity=capacity;
		// TODO: Add more logic here as necessary
	}
	
	/**
	 * Dequeues the first item from the queue and returns it.
	 * If the queue is empty but producers are still registered to this queue, 
	 * this method blocks until some item is available.
	 * If the queue is empty and no more items are planned to be added to this 
	 * queue (because no producers are registered), this method returns null.
	 * 
	 * @return The first item, or null if there are no more items
	 * @see #registerProducer()
	 * @see #unregisterProducer()
	 */
	public T dequeue() {
		synchronized (this) {
//			if (this.buffer.isEmpty() && this.producers == 0) {
//				return null;
//			}
//			while (this.buffer.isEmpty() && this.producers > 0) {
//				try {
//					wait();
//				} catch (Exception e) {
//					return null;
//				}
//			}//do nothing - block//wait
//			if (!this.buffer.isEmpty()){
//				T objectAnswer = this.buffer.remove();
//				notify();
//				return objectAnswer;
//			}
//			return null;
			if (isEmpty() && this.producers == 0) {
				return null;
			}
			while (isEmpty() && this.producers > 0) {
				try {
					this.wait();
				} catch (Exception e) {
					return null;
				}
			}//do nothing - block//wait
			if (!isEmpty()){
				T objectAnswer = dequeueQueue();
				this.notify();
				return objectAnswer;
			}
			this.notify();
			return null;
		}
	}

	/**
	 * Enqueues an item to the end of this queue. If the queue is full, this 
	 * method blocks until some space becomes available.
	 * 
	 * @param item Item to enqueue
	 */
	public void enqueue(T item) {
		synchronized (this) {
			while (this.size >= this.capacity) {
				try{
					this.wait();
				}
				catch(Exception e){
					return;
				}
			}//do nothing - block
			enqueueQueue(item);
			this.notify();
		}
	}

	/**
	 * Returns the capacity of this queue
	 * @return queue capacity
	 */
	public int getCapacity() {
		return this.capacity;
	}

	/**
	 * Returns the current size of the queue (number of elements in it)
	 * @return queue size
	 */
	public int getSize() {
		return this.buffer.length;
	}
	
	/**
	 * Registers a producer to this queue. This method actually increases the
	 * internal producers counter of this queue by 1. This counter is used to
	 * determine whether the queue is still active and to avoid blocking of
	 * consumer threads that try to dequeue elements from an empty queue, when
	 * no producer is expected to add any more items.
	 * Every producer of this queue must call this method before starting to 
	 * enqueue items, and must also call <see>{@link #unregisterProducer()}</see> when
	 * finishes to enqueue all items.
	 * 
	 * @see #dequeue()
	 * @see #unregisterProducer()
	 */
	public void registerProducer() {
		synchronized (this) {
			this.producers++;
		}
	}

	/**
	 * Unregisters a producer from this queue. See <see>{@link #registerProducer()}</see>.
	 * 
	 * @see #dequeue()
	 * @see #registerProducer()
	 */
	public void unregisterProducer() {
		synchronized (this) {
			this.producers--;
			notify();
		}
	}
	public void to_string(){
		for (T item :this.buffer) {
			if (item!=null)
			{
				System.out.println(item);
			}
		}
	}


//	################ QUEUE IMPLEMENTATION WITH ARRAY FUNCTIONS ####################


	public boolean isFull()
	{
		return (this.size == this.capacity);
	}

	// Queue is empty when size is 0
	public boolean isEmpty()
	{
		return (this.size == 0);
	}

	// Method to add an item to the queue.
	// It changes rear and size
	public void enqueueQueue(T item)
	{
		if (isFull())
			return;
		this.rear = (this.rear + 1)
				% this.capacity;
		this.buffer[this.rear] = item;
		this.size = this.size + 1;
	}

	// Method to remove an item from queue.
	// It changes front and size
	public T dequeueQueue()
	{
		if (isEmpty())
			return null;
		T item = this.buffer[this.front];
		this.front = (this.front + 1)
				% this.capacity;
		this.size = this.size - 1;
		return item;
	}
}
