import java.util.*;
import javax.swing.*;


public class GA {
	static ArrayList<String> individuals = new ArrayList<String>();
	static ToolBox tb = new ToolBox();
	static Crossover co;

	public static void main(String[] args) {

		Scanner s = new Scanner(System.in);
		long startmill,endmill;
		startmill=System.currentTimeMillis();
		int firstGeneration = s.nextInt();
		for (int i = 0; i < firstGeneration; i++) {
			// if((tb.StringToNumber(str)>=-1)&&(tb.StringToNumber(str)<=-0.5)){
			individuals.add(tb.Generate());
			// }
			// else i--;

		}
		int generation = 100;
		for (int i = 0; i < generation; i++) {
			startGA();
			tb.a = 1 + i;
		}
		endmill=System.currentTimeMillis();
		for(int p=0;p<individuals.size();p++)
		{
			tb.write(tb.StringToNumber(individuals.get(p)));
		}
		System.out.println("Total generations: 100.");
		System.out.println("the first generation has "+firstGeneration+" individuals.");
		System.out.println("Execution time: "+(endmill-startmill)+" ms.");
		System.out.println("Now show graph...");

		tb.select(results(individuals));
		ToolBox.DrawFrame frame = new ToolBox.DrawFrame();
	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     frame.setVisible(true);
		s.close();

	}

	public static void startGA() {
		individuals = tb.Roulette(individuals);
		int i, j;
		i = (int) (Math.random() * (individuals.size()));
		j = (int) (Math.random() * (individuals.size()));
		co = new Crossover(individuals.get(i), individuals.get(j));
		co.makeCrossover();
		individuals.remove(i);
		individuals.add(i, co.getA());
		individuals.remove(j);
		individuals.add(j, co.getB());
		for (int k = 0; k < individuals.size(); k++) {
			String temp;
			temp = tb.Hetermo(individuals.get(k));
			individuals.remove(k);
			individuals.add(k, temp);
			//tb.write(tb.StringToNumber(temp));
		}
		
	}

	public static ArrayList<Double> results(ArrayList<String> individuals) {
		ArrayList<Double> db = new ArrayList<Double>();
		for (int i = 0; i < individuals.size(); i++) {
			db.add(tb.StringToNumber(individuals.get(i)));
		}

		return db;
	}
	
	

}