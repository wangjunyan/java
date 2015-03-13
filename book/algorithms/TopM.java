public class TopM{
    private TopM(){}

    public static void main(String[] args){
        int M = Integer.parseInt(args[0]);
        MinPQ<Transaction> pq= new MinPQ<Transaction>(M+1);
        while(StdIn.hasNextLine()){
            String line = StdIn.readLine();
            Transaction transaction = new Transaction(line);
            pq.insert(transaction);
            if(pq.size() > M){
                pq.delMin();
            }
        }

        Stack<Transaction> stack = new Stack<Transaction>();
        for(Transaction transaction : pq)
            stack.push(transaction);
        for(Transaction transaction : stack)
            StdOut.println(transaction);
    }
}
