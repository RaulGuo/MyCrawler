package com.proudsmart.ark.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * @author: 黄文轩 @email: huangwenxuan@proudsmart.com
 *
 * @date: 2016年4月16日 @version: 1.0
 */
public class HeaderFactory {

	private static Map<String, Header[]> headerMap = null;

	static {
		headerMap = new HashMap<String, Header[]>();
		setCommonHeader();
		setGSHeader();
		setQCCHeader();
		setAlibabaHeader();
		setOnlineMapHeader();
	}
	
	private static void setOnlineMapHeader(){
		Map<String, String> header = new HashMap<String, String>();
		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header.put("Accept-Encoding", "gzip, deflate, sdch");
		header.put("Accept-Encoding", "zh-CN,zh;q=0.8,en;q=0.6");
		header.put("Cache-Control", "max-age=0");
		header.put("Host", "apis.map.qq.com");
		header.put("Upgrade-Insecure-Requests", "1");
		header.put("Connection", "keep-alive");
//		header.put("Cookie", "RK=FKXP4nk+Yp; tvfe_boss_uuid=cab8c426a9a68e0b; _qpsvr_localtk=0.21016438630700485; pgv_pvi=6578478080; pgv_si=s6979154944; o_cookie=787488544; pgv_info=ssid=s4953330696; pgv_pvid=6899171072; rv2=80E17A22E04E26E2978003A71EB3CD03A5F61818B52E233BD0; property20=86DF8326BE1AD1416A15571CA9B1718D6EADC92C7516CC590379BC07CCCCB4B21142C56EF65998CB; ptisp=ctc; ptcz=8601f9b824744dee517b7897917e5bdaa884893fc7e7678155f16cff521e8dfe; pt2gguin=o0787488544; uin=o0787488544; skey=@mkmgnc7qJ; mpuv=Cpar5VlbEmxmyhZkF7nqAg==");
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
		
		headerMap.put("OnlineMap", assemblyHeader(header));
	}

	/**
	 * 
	 */
	private static void setCommonHeader() {

		Map<String, String> common_header = new HashMap<String, String>();
		common_header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		common_header.put("Accept-Encoding", "gzip, deflate, sdch");
		common_header.put("Accept-Language", "zh-CN,zh;q=0.8");
		common_header.put("Cache-Control", "max-age=0");
		common_header.put("Connection", "keep-alive");
		common_header.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");

		headerMap.put("common", assemblyHeader(common_header));
	}

	/**
	 * 
	 */
	private static void setGSHeader() {

		Map<String, String> gs_eader = new HashMap<String, String>();
		gs_eader.put("Connection", "keep-alive");
		gs_eader.put("Cookie", "219429907259757");
		gs_eader.put("Host", "120.52.121.75:8443");
		gs_eader.put("User-Agent", "Mozilla/5.0 (Android;4.4.2;samsung;GT-N7100);Version/ErrorVersion;ISN_GSXT");

		headerMap.put("gs", assemblyHeader(gs_eader));
	}

	private static void setQCCHeader() {

		Map<String, String> qccHeader = new HashMap<String, String>();
		qccHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		qccHeader.put("Accept-Encoding", "gzip, deflate, sdch");
		qccHeader.put("Accept-Language", "zh-CN,zh;q=0.8");
		qccHeader.put("Cache-Control", "max-age=0");
		qccHeader.put("Connection", "keep-alive");
		String cookieStr = "PHPSESSID=cnuskn1i7ifvd8s3574nb81g66; "
				+ "pspt=%7B%22id%22%3A%221007965%22%2C%22pswd%22%3A%223773ab85255863e6194e166f7862400d%22%2C%22_code%22%3A%221706129c2d084c418ed7a3bd68faed51%22%7D; "
				+ "think_language=zh-cn; " + "CNZZDATA1254842228=1460175123-1458054988-null%7C1460825861; "
				+ "SERVERID=0359c5bc66f888586d5a134d958bb1be|1460826464|1460821968";
		qccHeader.put("Cookie", cookieStr);
		qccHeader.put("Host", "www.qichacha.com");
		qccHeader.put("Upgrade-Insecure-Requests", "1");
		qccHeader.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");

		HeaderFactory.setHeaderMap("qcc", qccHeader);
	}

	/**
	 * alibabaHeader
	 */
	private static void setAlibabaHeader() {
		Map<String, String> alibabaHeader = new HashMap<String, String>();
		alibabaHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		alibabaHeader.put("Accept-Encoding", "gzip, deflate, sdch, br");
		alibabaHeader.put("Accept-Language", "zh-CN,zh;q=0.8");
		alibabaHeader.put("Cache-Control", "max-age=0");
		// alibabaHeader.put("Connection", "keep-alive");
		String cookieStr = "JSESSIONID=8L781Huu1-j2wVMeqEeA6nBHRFgA-rsjRZpP-Sq6; ali_beacon_id=36.110.36.118.1467252449722.369641.8; cna=X4/JD5SsF38CASRuJHbap5W7; __cn_logon_id__=anshiguozhen; __last_loginid__=anshiguozhen; __cn_logon__=true; last_mid=b2b-1965170697; _csrf_token=1467259177237; _tmp_ck_0=\"99Wz0LQOJoB%2BMwP8%2Bb6iOPiiLXmwvWmKopay0sEjPttz9eCfGLgDeSsoA%2F%2BnDuvaj8VdaQFjgJ7kqXD1uwYYJ9mwBiqontquZsGsEpfq92UUJ%2FUxQWRsE0Oyg2H89QNHeZRac%2F%2Bjk2ejm%2FQ5J5g6PDvFaOyv0ysvlDNTHQbUYSPPtF3GZkWwQ%2Bcc%2BHgbgh1rOVsTKqWSZ1HdbdeA9Ll7lrfCudwjtYNIhR%2BqYy0wC%2B7CF5Q258rQuJq229c3Md5MU6RiNVE1oYvnUlpSYgao97kX5u2%2BT4ovn9idgs7TII6fnU%2FjplP8696jBAG%2Fp9NaTvOny3PaANkH5zrvWNoyU6korWrW1NOKl%2BWzRFXNG1YS1Kl7gURqO5fy3FO7Uic6p4vM5iJua3w%3D\"; isg=Alxc67rSZxuFXBNHUqJf0NUzLXpwdwD_Q_S-ATZdBMcqgfwLXuXQj9Lzl16D; h_keys=\"%u4e0a%u6d77%u94f6%u5409%u673a%u7535%u8bbe%u5907%u6709%u9650%u516c%u53f8#%u6d93%u5a43%u6363%u95be%u8dfa%u608f%u93c8%u8679%u6578%u7481%u60e7%ue62c%u93c8%u5910%u6aba%u934f%ue100%u5f83#%u6a61%u80f6%u78c1#%u78c1%u94c1#%u4e0a%u6d77%u94f6%u5409%u673a%u7535%u8bbe%u5907%u6709%u9650#%u4e9a%u745f%u58eb%u7537%u978b#%u4e9a%u745f%u58eb#%u94a2%u94c1%u4fa0#%u795e%u534e%u7164%u70ad\"; ad_prefer=\"2016/06/30 15:19:46\"; alisw=swIs1200%3D1%7C; ali_ab=36.110.36.118.1467252447540.1; alicnweb=touch_tb_at%3D1467269213249%7Clastlogonid%3Danshiguozhen; l=AtbWfGXnqEQZgj-I2m0l3UxkpobYdxqx";
		alibabaHeader.put("Cookie", cookieStr);
		alibabaHeader.put("Upgrade-Insecure-Requests", "1");
		alibabaHeader.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");

		HeaderFactory.setHeaderMap("alibaba", alibabaHeader);
	}

	/**
	 * @param headers
	 * @return
	 */
	public static Header[] assemblyHeader(Map<String, String> headers) {

		Header[] allHeader = new BasicHeader[headers.size()];
		int i = 0;
		for (String str : headers.keySet()) {
			allHeader[i] = new BasicHeader(str, headers.get(str));
			i++;
		}
		return allHeader;
	}

	/**
	 * @param type
	 * @return
	 */
	public static Header[] get(String type) {
		Header[] header = headerMap.get(type);
		if (header != null)
			return header;
		else
			return headerMap.get("common");
	}

	/**
	 * @param type
	 * @param header
	 */
	public static void setHeaderMap(String type, Map<String, String> header) {
		headerMap.put(type, assemblyHeader(header));

	}
}
