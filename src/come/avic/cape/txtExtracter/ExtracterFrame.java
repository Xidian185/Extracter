package come.avic.cape.txtExtracter;

import java.awt.Font;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class ExtracterFrame {

	JFrame frame;
	private TextField selectedFile;
	private JTextPane fileList;
	private List<String> fileLList;//存放待解析的所有文件
	private JProgressBar progressBar;//进度条
	/**
	 * Create the application.
	 */
	public ExtracterFrame() {
		initialize();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		fileLList = new LinkedList<String>();
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("楷体", Font.PLAIN, 15));
		frame.setBounds(100, 100, 689, 448);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("文件位置：");
		lblNewLabel.setFont(new Font("楷体", Font.PLAIN, 15));
		lblNewLabel.setBounds(10, 10, 85, 15);
		frame.getContentPane().add(lblNewLabel);
		
		JButton btnNewButton = new JButton("选择......");
		btnNewButton.addActionListener(new ActionListener() {
			//选择文件夹按钮被触发
			public void actionPerformed(ActionEvent e) {
				progressBar.setValue(0);
				JFileChooser jfc=new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
				jfc.showDialog(new JLabel(), "选择");
				File file = jfc.getSelectedFile();
				fileLList.clear();//先清除待解析文件列表，防止界面更换待解析路径导致错误
				
				String filePth = file.getAbsolutePath();
				selectedFile.setText(filePth);
				String fileName = jfc.getSelectedFile().getName();
				
				if(file.isDirectory()){
					//如果是目录，那么查看此目录下是否有可以解析的文件
					fileList.setText("");
					File[] files = file.listFiles();
					for(File f : files){
						if(f.isDirectory())
							continue;
						String fNameString = f.getName();
						if(Extracter.isCanExtract(fNameString)){
							fileLList.add(f.getAbsolutePath());
							String textString = fileList.getText();
							if(textString.isEmpty())
								fileList.setText(fNameString);
							else
								fileList.setText(textString + "\n" + fNameString);
						}
					}
				}else if(file.isFile()){
					//如果是一个文件，则检查是不是可以解析的文件
					if(Extracter.isCanExtract(fileName)){
						//可以解析
						fileList.setText(fileName);
						fileLList.add(file.getAbsolutePath());
					}
				}
			}
		});
		btnNewButton.setBounds(101, 6, 165, 23);
		frame.getContentPane().add(btnNewButton);
		
		selectedFile = new TextField();
		selectedFile.setEditable(false);
		selectedFile.setBounds(102, 51, 561, 23);
		frame.getContentPane().add(selectedFile);
		
		JLabel lblNewLabel_1 = new JLabel("已选择：");
		lblNewLabel_1.setFont(new Font("楷体", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(10, 59, 69, 15);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("可解析：");
		lblNewLabel_2.setFont(new Font("楷体", Font.PLAIN, 15));
		lblNewLabel_2.setBounds(10, 215, 69, 15);
		frame.getContentPane().add(lblNewLabel_2);
		
		fileList = new JTextPane();
		fileList.setEditable(false);
		fileList.setBounds(101, 100, 562, 218);
		JScrollPane jScrollPane = new JScrollPane(fileList);
		jScrollPane.setBounds(101, 100, 562, 218);
		frame.getContentPane().add(jScrollPane);
		
		JLabel lblNewLabel_3 = new JLabel("进度：");
		lblNewLabel_3.setFont(new Font("楷体", Font.PLAIN, 15));
		lblNewLabel_3.setBounds(10, 339, 54, 15);
		frame.getContentPane().add(lblNewLabel_3);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(101, 340, 562, 14);
		frame.getContentPane().add(progressBar);
		
		JButton btnNewButton_1 = new JButton("抽取");
		btnNewButton_1.addActionListener(new ActionListener() {
			//抽取按钮被触发
			public void actionPerformed(ActionEvent e) {
				extract(fileLList);
			}
		});
		btnNewButton_1.setFont(new Font("黑体", Font.PLAIN, 15));
		btnNewButton_1.setBounds(146, 376, 93, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("取消");
		btnNewButton_2.addActionListener(new ActionListener() {
			//放弃按钮被触发
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		btnNewButton_2.setFont(new Font("黑体", Font.PLAIN, 15));
		btnNewButton_2.setBounds(307, 376, 93, 23);
		frame.getContentPane().add(btnNewButton_2);
		
		JLabel lblNewLabel_4 = new JLabel("大数据技术研究部  版权所有");
		lblNewLabel_4.setFont(new Font("楷体", Font.PLAIN, 10));
		lblNewLabel_4.setBounds(523, 380, 140, 15);
		frame.getContentPane().add(lblNewLabel_4);
	}
	public void extract(List<String> filesList){
		if(filesList==null || filesList.size()==0)
			return;
		//使用多线程，并且更新进度条
		progressBar.setValue(0);
		int jobSize = filesList.size();
		//1、先在目的路径新建存放结果的文件夹
		String fileP = new File(filesList.get(0)).getPath();
		String dirString = fileP.substring(0,fileP.lastIndexOf("\\"));
		String targetPath = dirString + "\\extracterFiles";
		File targetDirectory = new File(targetPath);
		if(!targetDirectory.exists())
			targetDirectory.mkdir();

		ExecutorService pool = Executors.newFixedThreadPool(3);
		int barEach = 100 / jobSize;
		int barIndex = 100 % jobSize;//记录进度条
		progressBar.setValue(barIndex);
		LinkedList<Future> futers = new LinkedList<Future>();
		
		for(String fString : filesList){
			Callable c1 = new ExtracterThread(fString, targetDirectory);
			Future f1 = pool.submit(c1);
			futers.add(f1);
		}
		for(Future future : futers){
			try {
				if((boolean) future.get()){
					barIndex += barEach;
					progressBar.setValue(barIndex);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		pool.shutdown();
		JOptionPane.showMessageDialog(null, "解析完成", "提示", JOptionPane.INFORMATION_MESSAGE);
	}
}
