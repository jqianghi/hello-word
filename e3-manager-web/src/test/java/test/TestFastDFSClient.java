package test;

import java.net.URLDecoder;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import cn.e3mall.manager.utils.FastDFSClient;

public class TestFastDFSClient {

	private String local_filename = "E:/1.jpg";

	@Test
	public void test() throws Exception {
		// 读取fdfs_client.conf配置文件
		String conf_filename = this.getClass().getResource("/fdfs/fdfs_client.conf").getPath();
		// 处理中文路径
		conf_filename = URLDecoder.decode(conf_filename, "UTF-8");
		// 初始化Fdfs配置信息
		ClientGlobal.init(conf_filename);
		// 创建TrackerClient
		TrackerClient trackerClient = new TrackerClient();
		// 通过TrackerClient获取TrackerServer
		TrackerServer trackerServer = trackerClient.getConnection();
		// 创建StorageServer
		StorageServer storageServer = null;
		// 创建StorageClient
		StorageClient client = new StorageClient(trackerServer, storageServer);
		// 调用StorageClient的api方法实现上传\下载
		// 文件扩展名,不带.
		String file_ext_name = "jpg";
		String[] upload_file = client.upload_file(local_filename, file_ext_name, null);
		// 输出返回的file_id
		for (String string : upload_file) {
			System.out.println(string);
		}
	}

	@Test
	public void testFastDFSClient() throws Exception {
		// 创建FastDFSClient
		FastDFSClient client = new FastDFSClient();
		// 通过工具类的api方法,实现上传操作
		String extName = local_filename.substring(local_filename.lastIndexOf(".") + 1);
		String uploadFile = client.uploadFile(local_filename, extName);
		// 输出返回file_id
		System.out.println(uploadFile);
	}
}
