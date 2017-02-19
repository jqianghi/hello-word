package cn.e3mall.manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.utils.FastDFSClient;

@RequestMapping("/pic")
@Controller
public class PicController {

	@Value("${baseImgUrl}")
	private String baseImgUrl;

	/*
	 * @RequestMapping("/upload")
	 * 
	 * @ResponseBody public Map<String, Object> upload(MultipartFile uploadFile)
	 * { Map<String, Object> map = new HashMap<>(); if (uploadFile == null) {
	 * map.put("error", 1); map.put("message", "上传文件不能为空"); return map; } //
	 * 获取上传文件的原始名称 String originalFilename = uploadFile.getOriginalFilename();
	 * if (StringUtils.isEmpty(originalFilename)) { map.put("error", 1);
	 * map.put("message", "上传文件名称不能为空"); return map; } // 获取扩展名 String extName =
	 * originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
	 * 
	 * try { // 创建FastDFSClient工具类 FastDFSClient client = new FastDFSClient();
	 * String upload_path = client.uploadFile(uploadFile.getBytes(), extName);
	 * 
	 * // 组装完整的图片URL String imgUrl = baseImgUrl + upload_path;
	 * 
	 * map.put("error", 0); map.put("url", imgUrl); return map; } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace();
	 * map.put("error", 1); map.put("message", "上传失败"); return map; }
	 * 
	 * }
	 */

	@RequestMapping("/upload")
	@ResponseBody
	public String upload(MultipartFile uploadFile) {
		Map<String, Object> map = new HashMap<>();
		if (uploadFile == null) {
			map.put("error", 1);
			map.put("message", "上传文件不能为空");
			return JsonUtils.objectToJson(map);
		}
		// 获取上传文件的原始名称
		String originalFilename = uploadFile.getOriginalFilename();
		if (StringUtils.isEmpty(originalFilename)) {
			map.put("error", 1);
			map.put("message", "上传文件名称不能为空");
			return JsonUtils.objectToJson(map);
		}
		// 获取扩展名
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

		try {
			// 创建FastDFSClient工具类
			FastDFSClient client = new FastDFSClient();
			String upload_path = client.uploadFile(uploadFile.getBytes(), extName);

			// 组装完整的图片URL
			String imgUrl = baseImgUrl + upload_path;

			map.put("error", 0);
			map.put("url", imgUrl);
			return JsonUtils.objectToJson(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("error", 1);
			map.put("message", "上传失败");
			return JsonUtils.objectToJson(map);
		}

	}
}
