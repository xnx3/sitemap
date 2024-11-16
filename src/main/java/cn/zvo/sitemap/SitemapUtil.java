package cn.zvo.sitemap;

import java.util.List;
import com.xnx3.FileUtil;
import com.xnx3.Log;


/**
 * 创建 sitemap.xml
 * @author Administrator
 *
 */
public class SitemapUtil {
	public List<String> urls;
	
	public SitemapUtil(List<String> urls) {
		this.urls = urls;
	}
	
	/**
	 * 将生成的sitemap.xml 写出到文件
	 * @param path 传入如 D:\\sitemap.xml
	 * @return
	 */
	public void generateSitemapXML(String path) {
		String text = generateSitemapXML();
		
		//生成 sitemap.xml
		FileUtil.write(path, text);
	}
	

	/**
	 * 将生成的sitemap.txt 写出到文件
	 * @param path 传入如 D:\\sitemap.txt
	 * @return
	 */
	public void generateSitemapTXT(String path) {
		String text = generateSitemapTXT();
		
		//生成 sitemap.txt
		FileUtil.write(path, text);
	}
	
    public String generateSitemapXML() {
    	Log.info("generate sitemap.xml , size:"+urls.size()+"");
    	StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<urlset\n"
				+ "\txmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n"
				+ "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ "\txsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9\n"
				+ "\t\thttp://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
		
		for (int i = 0; i < urls.size(); i++) {
			sb.append(getSitemapUrl(urls.get(i), "0.4"));
		}
		
		//增加xml的末尾闭合标签
		sb.append("</urlset>");
		
		return sb.toString();
    }
    
    /**
     * 生成 sitemap.txt 的内容，也就是一行一个url
     * @return
     */
    public String generateSitemapTXT() {
    	Log.info("generate sitemap.txt , size:"+urls.size()+"");
    	StringBuffer sb = new StringBuffer();
		sb.append("");
		
		for (int i = 0; i < urls.size(); i++) {
			if(sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(urls.get(i));
		}
		
		return sb.toString();
    }
    
	/**
	 * SiteMap生成的url项 
	 * @param loc url
	 * @param priority 权重，如 1.00 、 0.5
	 * @return url标签的xml
	 */
	private String getSitemapUrl(String loc, String priority){
		if(loc.indexOf("http") == -1){
			loc = "http://"+loc;
		}
		return "<url>\n"
				+ "\t<loc>"+loc+"</loc>\n"
				+ "\t<priority>"+priority+"</priority>\n"
				+ "</url>\n";
	}
	
}
