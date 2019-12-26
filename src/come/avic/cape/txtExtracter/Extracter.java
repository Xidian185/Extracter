package come.avic.cape.txtExtracter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class Extracter {
	private static Set<String> fileType;
	static{
		fileType = new HashSet<String>();
		fileType.add("doc");
		fileType.add("docx");
		fileType.add("xls");
		fileType.add("xlsx");
		fileType.add("pdf");
		fileType.add("ppt");
		fileType.add("pptx");
	}
	public static boolean isCanExtract(String fileName){
		String strPostFix = fileName.substring(fileName.lastIndexOf(".")+1);
		return fileType.contains(strPostFix.toLowerCase());
	}
	public static String textToreader(String strFilePath) throws Exception{
		String content = "";
		String strPostFix = strFilePath.substring(strFilePath.lastIndexOf(".")+1);
        
		if (strPostFix.equalsIgnoreCase("doc")) {
			content = readWORD(strFilePath);
		} else if (strPostFix.equalsIgnoreCase("docx")) {
			content = readWORDX(strFilePath);
		} else if (strPostFix.equalsIgnoreCase("xls")) {
			content = readEXCEL(strFilePath);
		} else if (strPostFix.equalsIgnoreCase("xlsx")) {
			content = readEXCELX(strFilePath);
		} else if (strPostFix.equalsIgnoreCase("pdf")) {
			content = readPDF(strFilePath);
		} else if (strPostFix.equalsIgnoreCase("ppt")) {
			content = readPPT(strFilePath);
		} else if (strPostFix.equalsIgnoreCase("pptx")) {
			content = readPPTX(strFilePath);
		} else {
			content = "invalid file type!\n";
		}
		return content;
	}
	//xml or html
	public static String readXMLorHTML(String strFilePath) throws Exception{
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(new FileInputStream(strFilePath)));
		StringBuffer sb = new StringBuffer();
		String data = null;
		while ((data = br.readLine()) != null) {
			sb.append(data);
		}
		if(br != null)
			br.close();
		return sb.toString();
	}
	// txt
	public static String readTXT(String strFilePath) throws Exception {
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(new FileInputStream(strFilePath), "gbk"));
		StringBuffer sb = new StringBuffer();
		String data = null;
		while ((data = br.readLine()) != null) {
			sb.append(data);
		}
		if(br != null)
			br.close();
		return sb.toString();
	}
	
	// doc
	public static String readWORD(String strFilePath) throws Exception{
			
		FileInputStream fs = null;
		WordExtractor word = null;
		//String content = "";
		 fs = new FileInputStream(strFilePath);  
         word = new WordExtractor(fs);  
        String text = word.getText();  
        
        text = text.replaceAll("(\\r\\n){2,}", "\r\n");  
        text = text.replaceAll("(\\n){2,}", "\n");
        if(fs != null)
        	fs.close(); 
        if(word != null)
        	word.close();
		return text;
	}

	// docx
	public static String readWORDX(String strFilePath) throws Exception{
		FileInputStream fs = null;
		XWPFWordExtractor doc = null;
		fs = new FileInputStream(strFilePath);
		XWPFDocument XDocument = new XWPFDocument(fs);
		doc = new XWPFWordExtractor(XDocument);
		String str = doc.getText();
		if (doc != null)
			doc.close();
		if (fs != null)
			fs.close();
		return str;
	}
	//xls
	public static String readEXCEL(String strFilePath) throws Exception{
		FileInputStream fs = null;
		HSSFWorkbook wb = null;
		ExcelExtractor extractor = null;
		//String content = "";
		fs = new FileInputStream(strFilePath);
		wb = new HSSFWorkbook(new POIFSFileSystem(fs));
		extractor = new ExcelExtractor(wb);
		extractor.setFormulasNotResults(false);
		extractor.setIncludeSheetNames(true);
		String str = extractor.getText();
		//content = changeCharSet(str, "UTF-8");
		if(extractor != null)
			extractor.close();
		if(fs != null)
			fs.close();
		return str;
	}

	// xlsx
	public static String readEXCELX(String strFilePath) throws Exception{
		FileInputStream fs = null;
		XSSFWorkbook hwb = null;
		XSSFExcelExtractor extractor = null;
		//String content = "";
		fs = new FileInputStream(strFilePath);
		hwb = new XSSFWorkbook(fs);
		extractor = new XSSFExcelExtractor(hwb);
		String str = extractor.getText();
		//content = changeCharSet(str, "UTF-8");
		extractor.close();
		if(fs != null)
			fs.close();
		return str;
	}

	// pdf
	public static String readPDF(String strFilePath) throws Exception{
		File file = new File(strFilePath);
		PDDocument doc = null;
		PDFTextStripper pdfStripper = null;
		doc = PDDocument.load(file);
		pdfStripper = new PDFTextStripper();
		String str = pdfStripper.getText(doc);
		//content = changeCharSet(str, "UTF-8");
		if(doc != null)
			doc.close();
		return str;
	}

	// ppt
	public static String readPPT(String strFilePath) throws Exception{
		FileInputStream fs = null;
		POITextExtractor extractor = null;
		//String content = "";
		fs = new FileInputStream(strFilePath);
		extractor = ExtractorFactory.createExtractor(fs);
		String str = extractor.getText();
		//content = changeCharSet(str, "UTF-8");
		if(extractor != null)
			extractor.close();
		if(fs != null)
			fs.close();
		return str;
	}

	// pptx
	public static String readPPTX(String strFilePath) throws Exception{
		FileInputStream fs = null;
		POITextExtractor extractor = null;
		//String content = "";
		fs = new FileInputStream(strFilePath);
		extractor = ExtractorFactory.createExtractor(fs);
		String str = extractor.getText();
		//content = changeCharSet(str, "UTF-8");
		if(extractor != null)
			extractor.close();
		if(fs != null)
			fs.close();
		return str;
		}
}