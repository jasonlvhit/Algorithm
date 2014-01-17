public class Crossover {
	public Crossover(String a, String b) {
		this.a = a;
		this.b = b;
	}

	public void makeCrossover() {
		//int len = a.length() > b.length() ? b.length() : a.length();
		// System.out.println(len);
		int pivot = (int) (Math.random() * 100 % 8);
		pivot = 21 + pivot*4;

		// System.out.println(pivot);
		String temp1 = a.substring(pivot, a.length());
		String temp2 = b.substring(pivot, b.length());

		b = b.substring(0, pivot).concat(temp1);
		a = a.substring(0, pivot).concat(temp2);

		setA(a);
		setB(b);

	}

	public void setA(String a) {
		this.a = a;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getA() {
		return a;
	}

	public String getB() {
		return b;
	}

	private String a;
	private String b;
}
