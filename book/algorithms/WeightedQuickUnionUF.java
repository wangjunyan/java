public class WeightedQuickUnionUF{

    private int count;
    private int[] id;
    private int[] sz;

    public WeightedQuickUnionUF(int N){
        count = N;
        id = new int[N];
        sz = new int[N];
        for(int i=0; i<N; i++){
            id[i] = i;
            sz[i] = 1;
        }
    }

    public int count(){
        return count;
    }

    public int find(int p){
        while(p != id[p]) p = id[p];
        return p;
    }

    public boolean connected(int p, int q){
        return find(p)==find(q);
    }

    public void union(int p, int q){
        //if(connected(p,q)) return;
        int rootP = find(p);
        int rootQ = find(q);
        if(rootP == rootQ) return;
        if(sz[rootP] < sz[rootQ]){
            id[rootP] = rootQ;
            sz[rootQ] += sz[rootP];
        }else{
            id[rootQ] = rootP;
            sz[rootP] += sz[rootQ];
        }
        count--;
    }

    public static void main(String[] args) {
        int N = StdIn.readInt();
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(N);
        while (!StdIn.isEmpty()) {
            int p = StdIn.readInt();
            int q = StdIn.readInt();
            if (uf.connected(p, q)) continue;
            uf.union(p, q);
            StdOut.println(p + " " + q);
        }
        StdOut.println(uf.count() + " components");
    }
}
