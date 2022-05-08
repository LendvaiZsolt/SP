package sardinaspatterson;

import java.io.IOException;
import java.util.Arrays;

import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;

public class SardinasPatterson {

	public static void main(String[] args) throws Exception {

		String[] C = inputString();

		SardinasPatterson sp = new SardinasPatterson();
		boolean test = sp.sardinasPatterson(C);

		System.out.println();
		System.out.println("A bemeneti txt file tartalma:");
		for (int i = 0; i < C.length; i++) {
			System.out.println(C[i]);
		}
		System.out.println();

		System.out.println("A k�dsz�halmazzal egy�rtelm�en lehet dek�dolni? " + test);
		System.out.println();

		outputString(test);

		Object o = new Object();
		synchronized (o) {
			o.wait();
		}
	}

	// f�jlbeolvas�s
	public static String[] inputString() {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

		int result = fileChooser.showOpenDialog(fileChooser);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			System.out.print("A f�jl beolvas�s�nak helye: ");
			System.out.println(selectedFile.getAbsolutePath());
			System.out.println();
		}

		String filename = fileChooser.getSelectedFile().getAbsolutePath();

		ReadFile rf = new ReadFile();

		try {
			String[] lines = rf.readLines(filename);

			for (String line : lines) {
				return lines;
			}
		} catch (IOException e) {
			System.out.println("Unable to create " + filename + ": " + e.getMessage());
			return null;
		}
		return null;
	}

	//f�jl  ki�r�s
	public static void outputString(boolean b) {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int retrival = fileChooser.showSaveDialog(null);
		if (retrival == JFileChooser.APPROVE_OPTION) {
			try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile() + ".txt")) {
				fw.write(String.valueOf(b).toString());
				File selectedFile = fileChooser.getSelectedFile();
				System.out.print("A f�jl ki�r�s�nak helye: ");
				System.out.print(selectedFile.getAbsolutePath());
				System.out.println(".txt");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	// Ez az Igen/Nem v�g�llom�s.
	// Megkapja a C elem� inputString.
	// L�trehoz egy megfelel�en nagy �resz S string t�mb�t
	// �tadja a munk�t az C prefix keres� met�dusnak
	public boolean sardinasPatterson(String[] C) {
		int sLength = ((C.length) * (C.length + 1)) / 2;
		String[] S = new String[sLength];
		return sardinasPatterson(C, S);
	}

	// ha a C string t�mb "egy" eleme prefixe egy "m�sik" C t�mb beli elemnek akkor
	// a "m�sik" C t�mb beli elem suffix-�t bementj�k az S string t�mb soron
	// k�vetkez� elem�be, felt�ve, hogy ez az �j string �rt�k m�g nincs benne
	// a S string t�mbben
	private boolean sardinasPatterson(String[] C, String[] S) {

		int pivot = 0;
		int length = C.length;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				if (i != j && C[i].startsWith(C[j])) {
					String cache = getDanglingSuffix(C[j], C[i]);
					if (!Arrays.stream(S).anyMatch(cache::equals)) {
						S[pivot] = getDanglingSuffix(C[j], C[i]);
						pivot++;
					}
				}
			}
		}
		boolean unique = uniquelyDecodeable(C, S, pivot);
		return unique;
	}

	// az el�z� met�dusb�l kapott S �s az eredeti C t�mb elemeinek �sszehasonl�t�sa,
	// illetve az S elemeivel k�pzett �jabb �s �jabb S suffix elemek el��ll�t�sa
	private boolean uniquelyDecodeable(String[] C, String[] S, int pivot) {
		int cLength = C.length;

		for (int i = 0; i < pivot; i++) {
			for (int j = 0; j < cLength; j++) {

				// ha b�rmikor a suffixekb�l gener�lt S tartalmaz C k�dsz�elemet,
				// akkor nem egy�rtelm� a dek�dol�s

				if (S[i].equals(C[j])) {
					System.out.println("A kritikus k�dsz�: " + S[i]);
					return false;
				}

				// ha a suffixekb�l gener�lt S halmaz b�rmely eredeti k�dsz�nak
				// prefixe, akkor az �gy el��ll� suffixeket hozz�f�zz�k S-hez,
				// �s a sz�ml�l�t n�velj�k

				else if (C[j].startsWith(S[i])) {
					String cache = getDanglingSuffix(S[i], C[j]);
					if (!Arrays.stream(S).anyMatch(cache::equals)) {
						S[pivot] = cache;
						pivot++;
					}
				}

				// ha az eredeti k�dszavak b�rmelyike prefixe az �j S halmaznak,
				// akkor az �gy el��ll� suffixeket hozz�f�zz�k S-hez
				// �s a sz�ml�l�t n�velj�k

				else if (S[i].startsWith(C[j])) {
					String cache = getDanglingSuffix(C[j], S[i]);
					if (!Arrays.stream(S).anyMatch(cache::equals)) {
						S[pivot] = cache;
						pivot++;
					}
				}
			}
		}
		return true;
	}

	// a kapott prefixet lev�gja a szint�n kapott k�dsz� elej�r�l
	private String getDanglingSuffix(String prefix, String str) {
		return str.substring(prefix.length(), str.length());
	}
}