package cn.zvo.sitemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xnx3.BaseVO;
import com.xnx3.Log;
import com.xnx3.UrlUtil;

import cn.zvo.http.Http;
import cn.zvo.http.Response;

public class DefaultExecuteInterfaceImpl implements ExecuteInterface{

	@Override
	public void execute(String url,int depth, String threadName, Spider spider) {
		List<String> pageList = getPageAHref(url, spider);
		for(int i = 0; i < pageList.size(); i++) {
        	String pageUrl = pageList.get(i);
        	if (!pageUrl.startsWith("http")) {
        		//不是正常的网址，跳过
                continue;
            }
        	
        	spider.addUrl(pageUrl);
        	
        	if(spider.requestUrlMap.get(pageUrl) == null && spider.delayRequestUrlMap.get(pageUrl) == null) {
        		//没有获取过源码分析，那么进行分析
        		spider.delayRequestUrlMap.put(pageUrl, depth+1);
        	}
        }
	}
	

	/**
	 * 提取页面中出现的A标签
	 * @param pageUrl
	 * @return
	 */
    public List<String> getPageAHref(String pageUrl, Spider spider) {
        // 这里需要实现从给定页面 URL 获取链接的逻辑
        // 可以通过网页抓取或其他方式获取页面内容并提取链接
        // 示例：假设页面内容中包含以 <a> 标签表示的链接
    	List<String> urls = new ArrayList<String>();
    	//Log.info("pageUrl:"+pageUrl);
//    	if(requestUrlMap.get(pageUrl) != null) {
//    		//如果已经获取过源码了，那就不用再获取了
//    		return urls;
//    	}
    	
    	Http http = new Http();
    	http.setTimeout(spider.timeout);
    	String html = null;
    	int num = 0;
    	while(num < 3) {
    		num++;
    		try {
    			if(spider.requestSourceType - 1 == 0) {
    				Response res = http.get(pageUrl);
        			if(res != null && res.getCode() == 200) {
        				num = 20000;
        				html = res.getContent();
        			}
    			}else {
    				BaseVO vo = http.getPageResourceByCurl(pageUrl);
    				if(vo.getResult() - BaseVO.SUCCESS == 0) {
    					num = 20000;
    					html = vo.getInfo();
    				}
    			}
    		} catch (IOException e) {
    			Log.info(e.getMessage()+"\t"+pageUrl);
    		}
    	}
		
    	if(html != null) {
    		Document doc = Jsoup.parse(html);
    		doc.setBaseUri(pageUrl);
    		Elements eles = doc.getElementsByTag("a");
    		
    		for (int i = 0; i < eles.size(); i++) {
    			Element ele = eles.get(i);
//    			String href = ele.absUrl("href");
    			String href = ele.attr("abs:href");
//    			System.out.println(ele.attr("abs:href"));
    			if(href == null || href.length() == 0) {
    				continue;
    			}
    			if(href.indexOf("http") < 0) {
    				continue;
    			}
    			
    			//过滤掉描点 #xxx
    			if(href.indexOf("#") > -1) {
    				href = href.split("#")[0];
    			}
    			
    			String dom = UrlUtil.getDomain(href);
    			if(dom.equalsIgnoreCase(spider.domain)) {
    				//是同一个域名，属于同一个网站的，加入
    				urls.add(href);
    			}else {
    				//不是同一个网站的，舍弃
    			}
			}
    		
    	}else {
//    		try {
//    			FileUtil.appendText(new File(AutoPublishChineseSimpleToSftp.logPath+Global.errorLogPath), pageUrl+"\n");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
    	}
    	
    	return urls;
    }
}
