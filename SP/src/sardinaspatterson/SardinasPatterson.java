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

		System.out.println("A kódszóhalmazzal egyértelmûen lehet dekódolni? " + test);
		System.out.println();

		outputString(test);

		Object o = new Object();
		synchronized (o) {
			o.wait();
		}
	}

	// fájlbeolvasás
	public static String[] inputString() {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

		int result = fileChooser.showOpenDialog(fileChooser);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			System.out.print("A fájl beolvasásának helye: ");
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

	//fájl  kiírás
	public static void outputString(boolean b) {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int retrival = fileChooser.showSaveDialog(null);
		if (retrival == JFileChooser.APPROVE_OPTION) {
			try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile() + ".txt")) {
				fw.write(String.valueOf(b).toString());
				File selectedFile = fileChooser.getSelectedFile();
				System.out.print("A fájl kiírásának helye: ");
				System.out.print(selectedFile.getAbsolutePath());
				System.out.println(".txt");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	// Ez az Igen/Nem végállomás.
	// Megkapja a C elemû inputString.
	// Létrehoz egy megfelelõen nagy üresz S string tömböt
	// Átadja a munkát az C prefix keresó metódusnak
	public boolean sardinasPatterson(String[] C) {
		int sLength = ((C.length) * (C.length + 1)) / 2;
		String[] S = new String[sLength];
		return sardinasPatterson(C, S);
	}

	// ha a C string tömb "egy" eleme prefixe egy "másik" C tömb beli elemnek akkor
	// a "másik" C tömb beli elem suffix-ét bementjük az S string tömb soron
	// következõ elemébe, feltéve, hogy ez az új string érték még nincs benne
	// a S string tömbben
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

	// az elõzõ metódusból kapott S és az eredeti C tömb elemeinek összehasonlítása,
	// illetve az S elemeivel képzett újabb és újabb S suffix elemek elõállítása
	private boolean uniquelyDecodeable(String[] C, String[] S, int pivot) {
		int cLength = C.length;

		for (int i = 0; i < pivot; i++) {
			for (int j = 0; j < cLength; j++) {

				// ha bármikor a suffixekbõl generált S tartalmaz C kódszóelemet,
				// akkor nem egyértelmû a dekódolás

				if (S[i].equals(C[j])) {
					System.out.println("A kritikus kódszó: " + S[i]);
					return false;
				}

				// ha a suffixekbõl generált S halmaz bármely eredeti kódszónak
				// prefixe, akkor az így elõálló suffixeket hozzáfûzzük S-hez,
				// és a számlálót növeljük

				else if (C[j].startsWith(S[i])) {
					String cache = getDanglingSuffix(S[i], C[j]);
					if (!Arrays.stream(S).anyMatch(cache::equals)) {
						S[pivot] = cache;
						pivot++;
					}
				}

				// ha az eredeti kódszavak bármelyike prefixe az új S halmaznak,
				// akkor az így elõálló suffixeket hozzáfûzzük S-hez
				// és a számlálót növeljük

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

	// a kapott prefixet levágja a szintén kapott kódszó elejérõl
	private String getDanglingSuffix(String prefix, String str) {
		return str.substring(prefix.length(), str.length());
	}
}