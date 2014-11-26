package com.lin.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.lin.model.StudentEntity;

@Controller
@RequestMapping(value = "/")
public class MainAction {
	@RequestMapping(value = "/index")
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/down")
	public String down() {
		return "down";
	}

	@RequestMapping(value = "/json")
	@ResponseBody
	public Object getJson() {
		List<StudentEntity> list = new ArrayList<StudentEntity>();
		StudentEntity s1 = new StudentEntity("1993-01-28", "1", "hackcoder",
				"男");
		list.add(s1);
		return list;
	}

	/*
	 * 通过流的方式上传文件
	 * 
	 * @RequestParam("file") 将name=file控件得到的文件封装成CommonsMultipartFile 对象
	 */
	@RequestMapping("/fileUpload")
	public String fileUpload(@RequestParam("file") CommonsMultipartFile file)
			throws IOException {

		// 用来检测程序运行时间
		long startTime = System.currentTimeMillis();
		System.out.println("fileName：" + file.getOriginalFilename());

		try {
			// 获取输出流
			OutputStream os = new FileOutputStream("D:/" + new Date().getTime()
					+ file.getOriginalFilename());
			// 获取输入流 CommonsMultipartFile 中可以直接得到文件的流
			InputStream is = file.getInputStream();
			BufferedInputStream buf = new BufferedInputStream(is);
			byte[] b = new byte[1024];
			while (buf.read(b) != (-1)) {
				os.write(b, 0, b.length);
			}
			os.flush();
			os.close();
			is.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("方法一的运行时间：" + String.valueOf(endTime - startTime)
				+ "ms");
		return "success";
	}

	/*
	 * 采用file.Transto 来保存上传的文件
	 */
	@RequestMapping("/fileUpload2")
	public String fileUpload2(@RequestParam("file") CommonsMultipartFile file)
			throws IOException {
		long startTime = System.currentTimeMillis();
		System.out.println("fileName：" + file.getOriginalFilename());
		String path = "D:/" + new Date().getTime() + file.getOriginalFilename();

		File newFile = new File(path);
		// 通过CommonsMultipartFile的方法直接写文件（注意这个时候）
		file.transferTo(newFile);
		long endTime = System.currentTimeMillis();
		System.out.println("方法二的运行时间：" + String.valueOf(endTime - startTime)
				+ "ms");
		return "success";
	}

	/*
	 * 采用spring提供的上传文件的方法
	 */
	@RequestMapping("/springUpload")
	public String springUpload(HttpServletRequest request)
			throws IllegalStateException, IOException {
		long startTime = System.currentTimeMillis();
		// 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 检查form中是否有enctype="multipart/form-data"
		if (multipartResolver.isMultipart(request)) {
			// 将request变成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 获取multiRequest 中所有的文件名
			Iterator iter = multiRequest.getFileNames();

			while (iter.hasNext()) {
				// 一次遍历所有文件
				MultipartFile file = multiRequest.getFile(iter.next()
						.toString());
				if (file != null) {
					String path = "D:/" + file.getOriginalFilename();
					// 上传
					file.transferTo(new File(path));
				}

			}

		}
		long endTime = System.currentTimeMillis();
		System.out.println("方法三的运行时间：" + String.valueOf(endTime - startTime)
				+ "ms");
		return "success";
	}

	@RequestMapping("/downfile")
	public ResponseEntity<byte[]> downFile(String filename,
			HttpServletRequest req) throws IOException {
		String path = req.getSession().getServletContext()
				.getRealPath("upload")+"\\"+filename;
		
		filename = URLEncoder.encode((path), "UTF-8");
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.setContentDispositionFormData("attachment", new String("下载.CHM".getBytes("UTF-8"),"iso-8859-1"));
		return new ResponseEntity<byte[]>(
				FileUtils.readFileToByteArray(new File(path)), header,
				HttpStatus.OK);
	}
}
