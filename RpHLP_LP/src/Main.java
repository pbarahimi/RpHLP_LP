import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	private static double alpha = 0.2;
	private static double[][] flows = MyArray.read("w.txt");
//	private static double[][] fixedCharge = MyArray.read("fixedcharge.txt");
	private static double[][] coordinates = MyArray.read("coordinates.txt");
	private static double[][] distances = Distance.get(coordinates);
	private static int nVar = flows.length;
	private static int p = 5;
	private static double q = 0.95;
	private static int M = 2 * nVar * p;

	public static double q(int i, int j, int k, int m) {
		double result = 0;
		if (i != j) {
			if (j != k && j != m && i != k && i != m && k != m)
				result = Math.pow(q, 4);
			else if (j == k && j == m)
				result = Math.pow(q, 2);
			else if (i == k && i == m)
				result = Math.pow(q, 2);
			else if (i == k && j == m)
				result = Math.pow(q, 2);
			else if (i != k && j != m && k == m)
				result = Math.pow(q, 3);
			else if (i == k && m != k && j != m)
				result = Math.pow(q, 3);
			else if (i != k && m != k && j == m)
				result = Math.pow(q, 3);
			else if (j == k && k != m && i != m)
				result = Math.pow(q, 3);
			else if (i == m && m != k && j != k)
				result = Math.pow(q, 3);
			else if (j == k && i == m && k != m)
				result = Math.pow(q, 2);
		}
		return result;
	}

	/*
	 * public static void main(String[] args) throws FileNotFoundException{ File
	 * file = new File("Prob_Test.csv"); PrintWriter out = new
	 * PrintWriter(file);
	 * 
	 * // Objective function out.println("Minimize"); for (int r = 0; r < p;
	 * r++) { for (int i = 0; i < nVar; i++) { for (int j = 0; j < nVar; j++) {
	 * for (int k = 0; k < nVar; k++) { for (int m = 0; m < nVar; m++) { double
	 * CoEf = q(i,j,k,m); out.println("x" + i + "_" + j + "_" + k + "_" + m +
	 * "_" + r+","+CoEf); } } } } } out.close(); }
	 */

	public static void main(String[] args) throws FileNotFoundException {

		File file = new File("C:/Users/PB/git/RpHLP_LP/RpHLP_LP/ModelAndResults/model.lp");
		PrintWriter out = new PrintWriter(file);

		// Objective function
		out.println("Minimize");
		for (int r = 0; r < p; r++) {
			for (int i = 0; i < nVar; i++) {
				for (int j = 0; j < nVar; j++) {
					for (int k = 0; k < nVar; k++) {
						for (int m = 0; m < nVar; m++) {
							double Pr = q(i, j, k, m) * Math.pow((1 - q), r);
							double Wij = flows[i][j];
							double Cijkm = distances[i][k] + (1 - alpha)
									* distances[k][m] + distances[m][j];
							double CoEf = Wij * Cijkm * Pr;
							if (CoEf != 0) {
								out.println("+ " + CoEf + " x" + i + "_" + j
										+ "_" + k + "_" + m + "_" + r);
							}
						}
					}
				}
			}
		}

		/*for (int k = 0; k < nVar; k++) {
			out.print(" + " + fixedCharge[k][0] + " y" + k);
		}*/
		out.println();
		out.println("Subject to");

		// Constraint 1
		for (int k = 0; k < nVar; k++) {
			out.print(" + y" + k);
		}
		out.println(" = " + p);

		// Constraint 2
		for (int r = 0; r < p; r++) {
			for (int i = 0; i < nVar; i++) {
				for (int j = 0; j < nVar; j++) {
					for (int k = 0; k < nVar; k++) {
						for (int m = 0; m < nVar; m++) {

							out.print(" + x" + i + "_" + j + "_" + k + "_" + m
									+ "_" + r);
						}
					}
					out.println(" = 1");
				}
			}
		}
		out.println();

		// Constraint 3
		for (int r = 0; r < p; r++) {
			for (int i = 0; i < nVar; i++) {
				for (int j = 0; j < nVar; j++) {
					for (int k = 0; k < nVar; k++) {
						for (int m = 0; m < nVar; m++) {
							out.print(" + x" + i + "_" + j + "_" + k + "_" + m
									+ "_" + r);
						}
						out.println(" - y" + k + " <= 0");
					}
				}
			}
		}
		out.println();

		// Constraint 4
		for (int r = 0; r < p; r++) {
			for (int i = 0; i < nVar; i++) {
				for (int j = 0; j < nVar; j++) {
					for (int m = 0; m < nVar; m++) {
						for (int k = 0; k < nVar; k++) {
							out.print(" + x" + i + "_" + j + "_" + k + "_" + m
									+ "_" + r);
						}
						out.println(" - y" + m + " <= 0");
					}
				}
			}
		}
		out.println();

		// Constraint 5

		for (int i = 0; i < nVar; i++) {
			for (int j = 0; j < nVar; j++) {
				for (int k = 0; k < nVar; k++) {
					for (int m = 0; m < nVar; m++) {
						for (int r = 0; r < p; r++) {
							out.print(" + x" + i + "_" + j + "_" + k + "_" + m
									+ "_" + r);
						}
						out.println(" <= 1");
					}
				}
			}
		}

		// Constraint 6
		for (int i = 0; i < nVar; i++) {
			for (int j = 0; j < nVar; j++) {
				for (int k = 0; k < nVar; k++) {
					for (int t = 0; t < p - 1; t++) {

						for (int m = 0; m < nVar; m++) {
							for (int r = t + 1; r < p; r++) {
								out.print(" + x" + i + "_" + j + "_" + k + "_"
										+ m + "_" + r);
								out.print(" + x" + i + "_" + j + "_" + m + "_"
										+ k + "_" + r);
							}
						}

						out.println(" + " + M + " x" + i + "_" + j + "_"
								+ k + "_" + k + "_" + t + " <= " + M);
					}
				}
			}
		}
		
/*		// Constraint 7
		for (int i = 0; i < nVar; i++) {
			for (int j = 0; j < nVar; j++) {
				for (int m = 0; m < nVar; m++) {
					for (int t = 0; t < p - 1; t++) {

						for (int k = 0; k < nVar; k++) {
							for (int r = t + 1; r < p; r++) {
								out.print(" + x" + i + "_" + j + "_" + k + "_"
										+ m + "_" + r);
							}
						}
						out.println(" + " + (M - 1) + " x" + i + "_" + j + "_"
								+ m + "_" + m + "_" + t + " < " + (M-1));
					}
				}
			}
		}*/

		out.println("Binaries");

		// Binaries
		for (int k = 0; k < nVar; k++) {
			out.println("y" + k);
		}
		
		for (int r = 0; r < p; r++) {
			for (int i = 0; i < nVar; i++) {
				for (int j = 0; j < nVar; j++) {
					for (int k = 0; k < nVar; k++) {
						for (int m = 0; m < nVar; m++) {
								out.println("x" + i + "_" + j
										+ "_" + k + "_" + m + "_" + r);
						}
					}
				}
			}
		}
		out.close();

		// Test
		PrintWriter out1 = new PrintWriter(new File("ModelAndResults/Test.csv"));
		out1.append("Variable,Pr,Wij,Cijkm,Coefficient\n");
		for (int r = 0; r < p; r++) {
			for (int i = 0; i < nVar; i++) {
				for (int j = 0; j < nVar; j++) {
					for (int k = 0; k < nVar; k++) {
						for (int m = 0; m < nVar; m++) {
							double Pr = q(i, j, k, m) * Math.pow((1 - q), r);
							double Wij = flows[i][j];
							double Cijkm = distances[i][k] + (1 - alpha)
									* distances[k][m] + distances[m][j];
							double CoEf = Wij * Cijkm;
							out1.append("x" + i + "_" + j + "_" + k + "_" + m
									+ "_" + r + ",");
							out1.append(Pr + "," + Wij + "," + Cijkm + ","
									+ CoEf);
							out1.append("\n");
						}
					}
				}
			}
		}
		out1.close();
		
		// Solve the model
		ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start",
				 "C:/gurobi603/win64/bin/gurobi_cl",
				 "ResultFile=C:/Users/PB/git/RpHLP_LP/RpHLP_LP/ModelAndResults/Results.sol"
				 ,"C:/Users/PB/git/RpHLP_LP/RpHLP_LP/ModelAndResults/model.lp");
		 try {
			 pb.start();
			 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

	}

}