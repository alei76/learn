package com.lin.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.json.JSONException;

import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.fop.ExifRet;
import com.qiniu.api.fop.ImageExif;
import com.qiniu.api.fop.ImageInfo;
import com.qiniu.api.fop.ImageInfoRet;
import com.qiniu.api.fop.ImageView;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.net.CallRet;
import com.qiniu.api.rs.Entry;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.RSClient;
import com.qiniu.api.rs.URLUtils;
import com.qiniu.api.rsf.ListItem;
import com.qiniu.api.rsf.ListPrefixRet;
import com.qiniu.api.rsf.RSFClient;
import com.qiniu.api.rsf.RSFEofException;

public class QiniuFileUtil {
	private static Mac mac;
	static {
		mac = new Mac(Constant.ACCESS_KEY, Constant.SECRET_KEY);
	}

	/**
	 * 上传单个文件
	 * 
	 * @param fileName
	 * @param filePath
	 * @throws AuthException
	 * @throws JSONException
	 */
	public static void upload(String fileName, String filePath)
			throws AuthException, JSONException {
		PutPolicy putPolicy = new PutPolicy(Constant.BUCK_NAME);
		String uptoken = putPolicy.token(mac);
		PutExtra extra = new PutExtra();
		PutRet ret = IoApi.putFile(uptoken, fileName, filePath, extra);
	}

	/**
	 * 查看单个文件属性信息
	 * 
	 * @param key
	 * @return
	 */
	public static Entry stat(String key) {
		RSClient client = new RSClient(mac);
		Entry statRet = client.stat(Constant.BUCK_NAME, key);
		return statRet;
	}

	/**
	 * 生成上传授权uptoken
	 * 
	 * @return
	 * @throws AuthException
	 * @throws JSONException
	 */
	public static String getToken() throws AuthException, JSONException {
		PutPolicy putPolicy = new PutPolicy(Constant.BUCK_NAME);
		String uptoken = putPolicy.token(mac);
		return uptoken;
	}

	/**
	 * 下载私有单个文件路径
	 * 
	 * @param key
	 * @return
	 * @throws EncoderException
	 * @throws AuthException
	 */
	public static String down(String key) throws EncoderException,
			AuthException {
		String baseUrl = URLUtils.makeBaseUrl(Constant.DOMAIN, key);
		GetPolicy getPolicy = new GetPolicy();
		String downloadUrl = getPolicy.makeRequest(baseUrl, mac);
		return downloadUrl;
	}

	/**
	 * 移动单个文件
	 * 
	 * @param bucketSrc
	 * @param keySrc
	 * @param bucketDest
	 * @param keyDest
	 */
	public static void move(String bucketSrc, String keySrc, String bucketDest,
			String keyDest) {
		RSClient client = new RSClient(mac);
		client.move(bucketSrc, keySrc, bucketDest, keyDest);
	}

	/**
	 * 删除单个文件
	 * 
	 * @param key
	 * @return
	 */
	public static void delete(String key) {
		RSClient client = new RSClient(mac);
		client.delete(Constant.BUCK_NAME, key);
	}

	/**
	 * 查看图像属性
	 * 
	 * @param key
	 * @return
	 */
	public static ImageInfoRet fopImageInfo(String key) {
		String url = "http://" + Constant.DOMAIN + "/" + key;
		ImageInfoRet ret = ImageInfo.call(url);
		return ret;
	}

	/**
	 * 查看图片EXIF信息
	 * 
	 * @param key
	 * @return
	 */
	public static ExifRet fopImageExif(String key) {
		String url = "http://" + Constant.DOMAIN + "/" + key;
		ExifRet ret = ImageExif.call(url);
		return ret;
	}

	/**
	 * 生成图片预览
	 * 
	 * @param key
	 */
	public static CallRet fopImageView(String key) {
		String url = "http://" + Constant.DOMAIN + "/" + key;
		ImageView iv = new ImageView();
		iv.mode = 1;
		iv.width = 100;
		iv.height = 200;
		iv.quality = 1;
		iv.format = "jpg";
		CallRet ret = iv.call(url);
		return ret;
	}

	/**
	 * 批量获取文件列表
	 * 
	 * @param profix
	 *            文件前缀
	 * @param marker
	 * @return
	 */
	public static List<ListItem> listPrefix(String profix, String marker) {
		RSFClient client = new RSFClient(mac);
		List<ListItem> all = new ArrayList<ListItem>();
		ListPrefixRet ret = null;
		while (true) {
			ret = client.listPrifix(Constant.BUCK_NAME, profix, marker, 10);
			marker = ret.marker;
			all.addAll(ret.results);
			if (!ret.ok()) {
				break;
			}
		}
		if (ret.exception.getClass() != RSFEofException.class) {
		}
		return all;
	}

	public static void main(String[] args) {

	}
}
