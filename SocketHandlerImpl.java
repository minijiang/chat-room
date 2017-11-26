package test;

import java.awt.List;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import javax.swing.text.AbstractDocument.BranchElement;

/**
 * Created by feiliu on 2017/11/25.
 */
public class SocketHandlerImpl implements Runnable {
	private Socket socket = null;
	private DataInputStream dataInputStream = null;
	// 构建读处理流程
	private DataOutputStream dataOutputStream = null;
	private String tname = "";
	Collection<SocketHandlerImpl> socketHandlers = SessionUtils.getUsers(); // 获取所以的socket对象

	public SocketHandlerImpl(Socket socket, String tname) throws Exception {
		this.socket = socket;
		this.tname = tname;
		dataInputStream = new DataInputStream(socket.getInputStream());
		dataOutputStream = new DataOutputStream(socket.getOutputStream());
	}

	
	public void run() {

		new Thread(new Runnable() {

			public void run() {
				Scanner scanner = new Scanner(System.in);
				scanner.useDelimiter("\n");
				while (true) {
					try {
						System.out.print(name + ":");
						write(scanner.next().trim());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		while (true) {
			// 循环读取客户端发送的消息
			try {
				String data = dataInputStream.readUTF();
				handlerCMD(data);
			} catch (IOException e) {
				for (SocketHandlerImpl n : socketHandlers) {
					if (n != this) {
						n.write(tname + ",下线了!"); // 当有用户下线时 通知所有人
					}
				}
				break;
			}
		}
	}

	private void handlerCMD(String cmd) {
		if (cmd.indexOf("#") < 0) {
			System.out.println(tname + "@" + name + ":收到消息:" + cmd);
			return;
		}
		int index = cmd.indexOf("#");
		String type = cmd.substring(0, index);
		String body = cmd.substring(index + 1, cmd.length());

		// 类型#消息体
		// 消息转发类型 ->1 消息体: who-what
		// 登录成功 ->2 消息体: name
		// 3#context 默认转发所有成员
		// 4#refname-url 指定人转发文件
		// 5
		switch (Integer.parseInt(type)) {
		case 1:
			forwardMsg(body);
			break;
		case 2:
			loginMsg(body);
			break;
		case 3:
			forwardMsgs(body);
			break;
		case 4:
			try {
				sendFile(body);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			System.out.println("还未实现该消息类型");
			break;
		}
	}

	public String name = "匿名";

	public void loginMsg(String name) {
		this.name = name;
	}
	//发送消息
	public void write(String data) {
		try {
			dataOutputStream.writeUTF(data);
			dataOutputStream.flush();
		} catch (Exception e) {

			System.out.println("写入异常!");
		}
	}

	// 向特定的人转发消息
	private void forwardMsg(String body) {
		if (!body.contains("-")) {
			System.out.println("消息体不符合规范!");
			return;
		}
		String[] data = body.split("-");
		String who = data[0];
		String what = data[1];
		SocketHandlerImpl whoImpl = SessionUtils.getUsers(who);
		if (whoImpl != null) {
			whoImpl.write(what);
		}
	}

	// 向所有人发送消息
	private void forwardMsgs(String str) {
		if (str != "") {
			for (SocketHandlerImpl n : socketHandlers) {
				if (n != this) {
					n.write(str); // 默认向所有人转发
				}
			}
		}
	}

	// 发送文件
	private void sendFile(String body) throws Exception {
		String[] str = body.split("-");
		String name = str[0]; // 指向人
		String url = str[1]; // 文件路径
		int bufferSize = 1024; // 限定文件大小

		byte[] buf = new byte[bufferSize];
		if (url != null) {
			File file = new File(url);
			String fileName = file.getName();
			if (file != null) {
				SocketHandlerImpl Impl = SessionUtils.getUsers(name);
				DataInputStream s = new DataInputStream(
						new BufferedInputStream(new FileInputStream(file)));
				Impl.dataOutputStream.writeUTF("FileName：" + fileName
						+ ",FileSize" + file.length() + "字节");
				Impl.dataOutputStream.flush();
				while (true) {
					int read = 0;
					if (s != null) {
						read = s.read(buf);
					}

					if (read == -1) {
						break;
					}
					Impl.dataOutputStream.write(buf, 0, read);
				}
				Impl.dataOutputStream.flush();
				System.out.println("文件" + fileName + "传输完成");
			}
		}
	}
	public void acceptorFile(){
		
	}
}
