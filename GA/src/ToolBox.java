import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

class ToolBox {
	public double a = 1, derta = 0.02;
	public String[] elite = new String[3];
	public double[] eleite = new double[3];

	public String Generate() {
		String individual;
		/*建立格雷码的索引*/
		String[] graph = { "0000", "0001", "0011", "0010", "0110", "0111",
				"0101", "0100", "1100", "1101" };
		int ran;
		/*产生随机数，根据正负为产生的随机数赋值*/
		double tempdouble = Math.random() - 0.33333333;
		if (tempdouble > 0)
			individual = "0";
		else
			individual = "1";
			/*产生随机数，对整数部分赋值*/
		tempdouble = Math.random() - 0.5;
		if (tempdouble > 0)
			individual += "0000";
		else
			individual += "0001";
         /*对其余部分赋值*/
		for (int i = 0; i < 11; i++) {
			ran = (int) (Math.random() * 100) % 10;
			individual += graph[ran];
		}

		/*
		 * double tempDouble; char[] temp = new char[49]; for (int i = 0; i <
		 * 49; i++) { tempDouble = Math.random() - 0.5; if (tempDouble >= 0)
		 * temp[i] = '0'; else temp[i] = '1'; } for (int i = 1; i < 4; i++) {
		 * temp[i] = '0'; } individual = String.valueOf(temp); individual =
		 * individual.substring(0, 49);
		 */
		return individual;
	}

	@SuppressWarnings("unchecked")
	ArrayList<Integer> mysort(ArrayList<Double> fa) {
		ArrayList<Double> tempFa = new ArrayList<Double>();
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		ArrayList<Integer> numList = new ArrayList<Integer>();
		tempFa = (ArrayList<Double>) fa.clone();
		for (int i = 0; i < tempFa.size(); i++) {
			numList.add(i);
		}
		for (int i = 0; i < tempFa.size() && i < 30; i++) {
			int k = i;
			for (int j = i + 1; j < tempFa.size(); j++) {
				if (tempFa.get(j) > tempFa.get(k))
					k = j;
			}
			if (k != i) {
				int temp1 = numList.get(k);
				int temp2 = numList.get(i);
				numList.remove(k);
				numList.add(k, temp2);
				numList.remove(i);
				numList.add(i, temp1);
				double temp3 = tempFa.get(k);
				double temp4 = tempFa.get(i);
				tempFa.remove(k);
				tempFa.add(k, temp3);
				tempFa.remove(i);
				tempFa.add(i, temp4);
			}
		}
		for (int i = 0; i < 30; i++) {
			if (i < numList.size()) {
				tempList.add(numList.get(i));
			}
		}
		return tempList;
	}

	ArrayList<String> Roulette(ArrayList<String> str) {
		ArrayList<Double> fa = Fitness(str);
		double sum = 0.0;
		double fitnessTemp;
		ArrayList<Double> selected = new ArrayList<Double>();
		ArrayList<String> copy = new ArrayList<String>();
		ArrayList<Integer> frequency = new ArrayList<Integer>();
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		for (int l = 0; l < str.size(); l++) {
			frequency.add(0);
		}
		tempList = mysort(fa);
		int len = fa.size();
		for (double i : fa) {
			sum += i;
		}
		double temp = 0;
		int ftemp;
		for (int i = 0; i < len; i++) {
			// if (fa.get(i)>0){
			temp += fa.get(i);
			selected.add(temp / sum);
			// }
		}
		for (int i = 0; i < len * 2; i++) {
			double p = Math.random();
			int j = mid_find(p, selected);
			ftemp = frequency.get(j) + 1;
			frequency.remove(j);
			frequency.add(j, ftemp);
		}
		for (int i = 0; i < len; i++) {
			if (frequency.get(i) > 0)
				copy.add(str.get(i));
		}
		for (int i = 0; i < tempList.size(); i++) {
			copy.add(str.get(i));
		}
		return copy;
	}

	int mid_find(double x, ArrayList<Double> b) {
		int l = 0, r = b.size();
		if (x <= b.get(l))
			return l;
		while (l < r) {
			int mid = (l + r) / 2;
			if (x - b.get(l) > 0 && x - b.get(l + 1) <= 0) {
				l++;
				break;
			}
			if (x > b.get(mid)) {
				l = mid;
			} else {
				r = mid;
			}
		}
		return l;
	}

	public ArrayList<Double> Fitness(ArrayList<String> origin) {
		ArrayList<Double> fit = new ArrayList<Double>();
		double temp, k;
		for (int i = 0; i < origin.size(); i++) {
			temp = StringToNumber(origin.get(i));
			if (temp < -1) {
				fit.add(0.0);
			} else {
				k = Math.abs(Math.sin(10 * Math.PI * temp) + 10 * Math.PI
						* temp * Math.cos(10 * Math.PI * temp));
				fit.add((double) a * Math.exp(-k));
			}
		}
		return fit;
	}

	public double StringToNumber(String a) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("0000", "0");
		map.put("0001", "1");
		map.put("0011", "2");
		map.put("0010", "3");
		map.put("0110", "4");
		map.put("0111", "5");
		map.put("0101", "6");
		map.put("0100", "7");
		map.put("1100", "8");
		map.put("1101", "9");

		String integer = map.get(a.substring(1, 5));
		// System.out.println(a.length());

		String doublenumber = new String();

		for (int i = 4; i <= (a.length() - 5); i += 4) {
			doublenumber += map.get(a.substring(i + 1, i + 5));
		}

		doublenumber = integer + "." + doublenumber;

		double result = Double.parseDouble(doublenumber);

		if (a.charAt(0) == '1') {
			result = 0 - result;
		}
		return result;
		// System.out.println(result);
	}

	public String NumberToString(double d) {
		String buffer;
		String[] graph = { "0000", "0001", "0011", "0010", "0110", "0111",
				"0101", "0100", "1100", "1101" };
		if (d >= 0) {
			buffer = "0";
		} else
			buffer = "1";
		if (d >= 1) {
			buffer += "0001";
			d--;
		}
		d = Math.abs(d);
		int temp;
		for (int i = 0; i < 11; i++) {
			d *= 10;
			temp = (int) d;
			buffer += graph[temp];
			d -= temp;
		}

		return buffer;
	}

	public static double destiny(double input) {
		return input*Math.sin(10 * Math.PI * input) + 1.0;
	}

	public String Hetermo(String str) {
	    /*建立索引*/
		String[] graph = { "0000", "0001", "0011", "0010", "0110", "0111",
				"0101", "0100", "1100", "1101" };
		char[] temp = str.toCharArray();
		char[] temp1;
		int index, count;
		/*产生随机数，决定变异次数*/
		count = (int) (Math.random() * 2);
		for (int i = 0; i < count; i++) {
            /*随机产生变异的下标*/
			index = (int) (Math.random() * 7);
            /*随机产生被替换的十进制数*/
			count = (int) (Math.random() * 100 % 9);
			temp1 = graph[count].toCharArray();
			for (int j = 0; j < 4; j++) {
				temp[j + index * 4 + 21] = temp1[j];
			}
		}
		/*将char数组转化为String*/
		str = String.valueOf(temp);
		str = str.substring(0, 49);
		return str;
	}

	void select(ArrayList<Double> a)// 传进来的表示剩下的点的值
	{
		double[] hax = new double[40];// 存放不同种群的数组
		int[] haxInx = new int[40];// 存放对应种群数量的数组
		DecimalFormat format = new DecimalFormat("0.000000");
		for (int k = 0; k < 30; k++) {
			hax[k] = haxInx[k] = 0;
		}
		int len = a.size();
		for (int i = 0; i < len; i++) {
			int temp = (int) (a.get(i) / 0.05);// 利用哈希函数将不同种群映射到不同的位置
			if (temp > 0) {
				hax[temp / 2] += a.get(i);
				haxInx[temp / 2]++;
			} else {
				hax[20 - (temp / 2 - 1)] += a.get(i);
				haxInx[20 - (temp / 2 - 1)]++;
			}
		}
		// hax[20]=0.000000;
		// haxInx[20]=1;
		len = hax.length;
		for (int i = 0; i < len; i++) {
			if (haxInx[i] > 0) {
				hax[i] = hax[i] / haxInx[i];
				System.out.println(format.format(hax[i]));// 打印结果
				// System.out.println(Math.abs(Math.sin(10 * Math.PI * hax[i])
				// + hax[i] * Math.cos(10 * Math.PI * hax[i])));
			}
		}
	}

	public void write(double x) {
		try {
			FileOutputStream fos = new FileOutputStream("log.txt",true);
			PrintWriter pw = new PrintWriter(fos);
			pw.println(x);
			pw.flush();
			pw.close();
			pw.close();
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	static  class DrawFrame extends JFrame
	{
	   public DrawFrame()
	   {
	      setTitle("Graphic");
	      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

	      // add panel to frame

	      DrawPanel panel = new DrawPanel();
	      add(panel);
	   }

	   public static final int DEFAULT_WIDTH = 400;
	   public static final int DEFAULT_HEIGHT = 400;
	}

	static class DrawPanel extends JPanel
	{
	   public void paintComponent(Graphics g) //throws IOException
	   {
	      super.paintComponent(g);
	      Graphics2D g2 = (Graphics2D) g;

		  /*画出坐标图*/
	      g2.drawLine(200,50,200,350);
		  g2.drawLine(50,300,350,300);//横纵坐标
		  g2.drawString("-1",125,310);
		  g2.drawString("0",205,310);
		  g2.drawString("1",275,310);
		  g2.drawString("2",350,310);//横坐标上的示数
		  g2.drawString("-1",205,350);
		  g2.drawString("1",205,250);
		  g2.drawString("2",205,200);
		  g2.drawString("3",205,150);
		  int x1,x2,y1,y2;
		  g2.setColor(Color.BLUE);
		  for(double i=-(1.0);i<=2.0;i=i+0.01)//画出整个图像
		  {
		    x1=(int)(200+75*i);
			y1=(int)(300-50*destiny(i));
			x2=(int)(200+75*(i+0.01));
			y2=(int)(300-50*destiny(i+0.01));
		    g2.drawLine(x1,y1,x2,y2);
		  }

		  g2.setColor(Color.RED);//设置点的颜色

		  /*从文件中读取所有点，并显示*/

		  String str=" ";
		  int xpos,ypos;
		  double x,y;
		  int r=3;
		  try
		  {
		    BufferedReader in = new BufferedReader(new FileReader("log.txt"));
		    while(true)
			{
			   str=in.readLine();
			   if(str==null)
			   break;
			   x = Double.parseDouble(str);
			   y = destiny(x);
			   xpos = (int) (200 + 75 * x);
			   ypos = (int) (300 - 50 * y);
			   g2.drawOval(xpos,ypos,r,r);//在图像上圈出相应的点
			}
		    in.close();
		  }
		  catch(IOException ex)
		  {
		    System.out.println(ex);
		  }
	    }

	}

}
