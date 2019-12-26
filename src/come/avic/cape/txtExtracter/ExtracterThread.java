package come.avic.cape.txtExtracter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;

@SuppressWarnings("rawtypes")
public class ExtracterThread implements Callable {
	
	private String srcFilePath;//待读取的文件
	private File dstFilePath;//待写入的文件夹
	
	public ExtracterThread(String srcFilePath, File dstFilePath) {
		this.srcFilePath = srcFilePath;
		this.dstFilePath = dstFilePath;
	}
	
	@Override
	public Object call() {

		String fileFullNameString = srcFilePath.substring(srcFilePath.lastIndexOf("\\"),srcFilePath.lastIndexOf("."));
		String tartgetFile = dstFilePath.getAbsolutePath() + fileFullNameString +"-extract" + ".txt";
		File targetFile = new File(tartgetFile);
		File srcFile = new File(srcFilePath);
		if(targetFile.exists()){
			targetFile.delete();
		}
		BufferedWriter bWriter = null;
		try{
			targetFile.createNewFile();
			
			String contentString = Extracter.textToreader(srcFile.getAbsolutePath());
			
			bWriter = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(targetFile.getAbsolutePath())));
			bWriter.write(contentString);
		}catch (Exception e) {
			System.err.print(fileFullNameString + "提取异常！");
		}finally {
			if(bWriter != null)
				try {
					bWriter.close();
				} catch (IOException e) {
					System.err.print(fileFullNameString + "提取异常！");
				}
		}
		return true;
	}
 
	
}