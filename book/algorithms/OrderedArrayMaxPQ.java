public class OrderedArrayMaxPQ<Key extends Comparable<Key>>{
    private Key[] pq;
    private int N;

    public OrderedArrayMaxPQ(int capacity){
        pq = (Key[])(new Comparable[capacity]);
        N = 0;
    }

    public boolean isEmpty(){return N==0;}
    public int size() {return N;}
    public Key delMax() {return pq[--N];}

    public void insert(Key key){
        int i = N-1;
        while(i>=0 && less(key, pq[i])){
            pq[i+1] = pq[i];
            i--;
        }
        pq[i+1] = key;
        N++;
    }

    private boolean less(Key v, Key w) {
        return (v.compareTo(w) < 0);
    }

    private void exch(int i, int j) {
        Key key = pq[i];
        pq[i] = pq[j];
        pq[j] = key;
    }

    public static void main(String[] args) {
        OrderedArrayMaxPQ<String> pq = new OrderedArrayMaxPQ<String>(10);
        pq.insert("this");
        pq.insert("is");
        pq.insert("a");
        pq.insert("test");
        while (!pq.isEmpty()) StdOut.println(pq.delMax());

    }
}
