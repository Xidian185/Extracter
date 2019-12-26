package come.avic.cape.txtExtracter;

import java.awt.EventQueue;

public class Main {
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExtracterFrame window = new ExtracterFrame();
					window.frame.setVisible(true);
					window.frame.setTitle("文档提取器--V1.0.0");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
